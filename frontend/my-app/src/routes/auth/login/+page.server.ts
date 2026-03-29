import { fail, redirect } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';
import { BackendHttpError } from '$lib/server/backend';
import { setAuthCookies } from '$lib/server/cookies';
import { loginWithPassword } from '$lib/server/auth';

export const load: PageServerLoad = async ({ locals, url }) => {
	if (locals.session.isAuthenticated) {
		throw redirect(302, url.searchParams.get('redirectTo') || '/account');
	}

	return {
		redirectTo: url.searchParams.get('redirectTo') || '/account'
	};
};

export const actions: Actions = {
	default: async ({ request, cookies, fetch }) => {
		const formData = await request.formData();

		const email = String(formData.get('email') ?? '').trim();
		const password = String(formData.get('password') ?? '');
		const redirectTo = String(formData.get('redirectTo') ?? '/account');

		if (!email || !password) {
			return fail(400, {
				message: 'Email and password are required.',
				values: { email, redirectTo }
			});
		}

		try {
			const tokens = await loginWithPassword(fetch, { email, password });
			setAuthCookies(cookies, tokens);

			throw redirect(303, redirectTo);
		} catch (error) {
			if (error instanceof BackendHttpError) {
				return fail(error.status === 401 ? 400 : error.status, {
					message: error.message,
					values: { email, redirectTo }
				});
			}

			throw error;
		}
	}
};