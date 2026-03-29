import { fail } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';
import { BackendHttpError } from '$lib/server/backend';
import { getAdminUser, updateAdminUser } from '$lib/server/admin';

export const load: PageServerLoad = async ({ locals, fetch, params }) => {
	const userId = Number(params.userId);

	const item = locals.accessToken ? await getAdminUser(fetch, locals.accessToken, userId) : null;

	return {
		item
	};
};

export const actions: Actions = {
	default: async ({ request, locals, fetch, params }) => {
		if (!locals.accessToken) {
			return fail(401, {
				message: 'Unauthorized',
				values: {
					displayName: '',
					enabled: true,
					roles: [] as string[]
				}
			});
		}

		const userId = Number(params.userId);
		const formData = await request.formData();

		const displayName = String(formData.get('displayName') ?? '').trim();
		const enabled = formData.get('enabled') === 'on';
		const roles = formData
			.getAll('roles')
			.map((value) => String(value))
			.filter(Boolean);

		try {
			const item = await updateAdminUser(fetch, locals.accessToken, userId, {
				displayName,
				enabled,
				roles
			});

			return {
				success: true,
				message: 'User updated successfully.',
				item,
				values: {
					displayName: item.displayName,
					enabled: item.enabled,
					roles: item.roles
				}
			};
		} catch (error) {
			if (error instanceof BackendHttpError) {
				return fail(error.status, {
					message: error.message,
					values: {
						displayName,
						enabled,
						roles
					}
				});
			}

			throw error;
		}
	}
};