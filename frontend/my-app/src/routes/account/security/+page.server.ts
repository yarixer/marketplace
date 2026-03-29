import { fail } from '@sveltejs/kit';
import type { Actions } from './$types';
import { BackendHttpError } from '$lib/server/backend';
import { changePassword } from '$lib/server/account';

export const actions: Actions = {
	default: async ({ request, locals, fetch }) => {
		if (!locals.accessToken) {
			return fail(401, { message: 'Unauthorized' });
		}

		const formData = await request.formData();

		const oldPassword = String(formData.get('oldPassword') ?? '');
		const newPassword = String(formData.get('newPassword') ?? '');
		const confirmNewPassword = String(formData.get('confirmNewPassword') ?? '');

		if (!oldPassword || !newPassword || !confirmNewPassword) {
			return fail(400, {
				message: 'All password fields are required.'
			});
		}

		try {
			await changePassword(fetch, locals.accessToken, {
				oldPassword,
				newPassword,
				confirmNewPassword
			});

			return {
				success: true,
				message: 'Password changed successfully.'
			};
		} catch (error) {
			if (error instanceof BackendHttpError) {
				return fail(error.status, {
					message: error.message
				});
			}

			throw error;
		}
	}
};