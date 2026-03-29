import { redirect } from '@sveltejs/kit';
import type { LayoutServerLoad } from './$types';
import { getSellerDashboard } from '$lib/server/seller';

export const load: LayoutServerLoad = async ({ locals, fetch, url }) => {
	if (!locals.session.isAuthenticated || !locals.session.user || !locals.accessToken) {
		throw redirect(302, `/auth/login?redirectTo=${encodeURIComponent(url.pathname + url.search)}`);
	}

	if (!locals.session.user.seller) {
		throw redirect(302, '/account/general');
	}

	const dashboard = await getSellerDashboard(fetch, locals.accessToken);

	return {
		user: locals.session.user,
		dashboard
	};
};