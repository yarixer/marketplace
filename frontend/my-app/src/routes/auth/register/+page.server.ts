import { fail, redirect } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';
import { BackendHttpError } from '$lib/server/backend';
import { setAuthCookies } from '$lib/server/cookies';
import { registerAccount } from '$lib/server/auth';

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
		const displayName = String(formData.get('displayName') ?? '').trim();
		const password = String(formData.get('password') ?? '');
		const redirectTo = String(formData.get('redirectTo') ?? '/account');

		if (!email || !displayName || !password) {
			return fail(400, {
				message: 'Email, display name and password are required.',
				values: { email, displayName, redirectTo }
			});
		}

		try {
			const tokens = await registerAccount(fetch, { email, displayName, password });
			setAuthCookies(cookies, tokens);

			throw redirect(303, redirectTo);
		} catch (error) {
			if (error instanceof BackendHttpError) {
				return fail(error.status, {
					message: error.message,
					values: { email, displayName, redirectTo }
				});
			}

			throw error;
		}
	}
};