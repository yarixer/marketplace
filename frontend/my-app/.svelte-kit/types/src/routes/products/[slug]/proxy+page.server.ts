// @ts-nocheck
import { fail, redirect } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';
import { BackendHttpError } from '$lib/server/backend';
import {
	createBuyerOrder,
	getBuyerLibrary,
	mockCompleteBuyerOrder
} from '$lib/server/buyer';
import { getPublicProductBySlug } from '$lib/server/public';

export const load = async ({ params, locals, fetch }: Parameters<PageServerLoad>[0]) => {
	const product = await getPublicProductBySlug(fetch, params.slug);

	let isOwned = false;
	let downloadHref: string | null = null;

	if (locals.session.isAuthenticated && locals.accessToken) {
		const library = await getBuyerLibrary(fetch, locals.accessToken);
		const ownedItem = library.find((item) => item.productId === product.productId);

		isOwned = Boolean(ownedItem);
		downloadHref = ownedItem ? `/account/library/download/${product.productId}` : null;
	}

	return {
		session: locals.session,
		product,
		isOwned,
		downloadHref
	};
};

export const actions = {
	buyNow: async ({ locals, fetch, params }: import('./$types').RequestEvent) => {
		if (!locals.session.isAuthenticated || !locals.accessToken) {
			throw redirect(302, `/auth/login?redirectTo=/products/${params.slug}`);
		}

		const product = await getPublicProductBySlug(fetch, params.slug);

		try {
			const createdOrder = await createBuyerOrder(fetch, locals.accessToken, product.productId);
			await mockCompleteBuyerOrder(fetch, locals.accessToken, createdOrder.orderId);
		} catch (error) {
			if (error instanceof BackendHttpError) {
				return fail(error.status, {
					buyMessage: error.message
				});
			}

			throw error;
		}

		throw redirect(303, '/account/library');
	}
};;null as any as Actions;