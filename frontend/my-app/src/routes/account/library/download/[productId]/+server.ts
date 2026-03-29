import { redirect } from '@sveltejs/kit';
import type { RequestHandler } from './$types';
import { BackendHttpError } from '$lib/server/backend';
import { getBuyerDownloadLink } from '$lib/server/buyer';

export const GET: RequestHandler = async ({ locals, fetch, params }) => {
	if (!locals.session.isAuthenticated || !locals.accessToken) {
		throw redirect(302, '/auth/login?redirectTo=/account/library');
	}

	const productId = Number(params.productId);
	if (!Number.isFinite(productId)) {
		throw redirect(303, '/account/library?downloadError=Invalid%20product%20id');
	}

	try {
		const result = await getBuyerDownloadLink(fetch, locals.accessToken, productId);
		throw redirect(302, result.url);
	} catch (error) {
		if (error instanceof BackendHttpError) {
			const message = encodeURIComponent(error.message || 'Unable to create download link');
			throw redirect(303, `/account/library?downloadError=${message}`);
		}

		throw error;
	}
};