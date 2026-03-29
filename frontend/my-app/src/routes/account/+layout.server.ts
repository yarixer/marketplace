import { redirect } from '@sveltejs/kit';
import type { LayoutServerLoad } from './$types';
import { getWallet } from '$lib/server/account';

export const load: LayoutServerLoad = async ({ locals, fetch, url }) => {
	if (!locals.session.isAuthenticated || !locals.session.user || !locals.accessToken) {
		throw redirect(302, `/auth/login?redirectTo=${encodeURIComponent(url.pathname + url.search)}`);
	}

	const wallet = await getWallet(fetch, locals.accessToken);

	return {
		user: locals.session.user,
		wallet
	};
};