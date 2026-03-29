// @ts-nocheck
import { redirect } from '@sveltejs/kit';
import type { LayoutServerLoad } from './$types';

export const load = async ({ locals, url }: Parameters<LayoutServerLoad>[0]) => {
	if (!locals.session.isAuthenticated || !locals.session.user || !locals.accessToken) {
		throw redirect(302, `/auth/login?redirectTo=${encodeURIComponent(url.pathname + url.search)}`);
	}

	if (!locals.session.user.roles.includes('ADMIN')) {
		throw redirect(302, '/account/general');
	}

	return {
		user: locals.session.user
	};
};