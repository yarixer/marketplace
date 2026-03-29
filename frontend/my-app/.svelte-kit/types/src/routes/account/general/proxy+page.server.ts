// @ts-nocheck
import { fail } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';
import { BackendHttpError } from '$lib/server/backend';
import { becomeSeller, updateProfile } from '$lib/server/account';

export const load = async ({ parent }: Parameters<PageServerLoad>[0]) => {
	const { user } = await parent();

	return {
		user
	};
};

export const actions = {
	updateProfile: async ({ request, locals, fetch }: import('./$types').RequestEvent) => {
		if (!locals.accessToken) {
			return fail(401, { profileMessage: 'Unauthorized' });
		}

		const formData = await request.formData();
		const displayName = String(formData.get('displayName') ?? '').trim();

		if (!displayName) {
			return fail(400, {
				profileMessage: 'Display name is required.',
				profileValues: { displayName }
			});
		}

		try {
			const user = await updateProfile(fetch, locals.accessToken, { displayName });

			locals.session = {
				isAuthenticated: true,
				user
			};

			return {
				profileSuccess: true,
				profileMessage: 'Profile updated successfully.',
				profileValues: { displayName: user.displayName }
			};
		} catch (error) {
			if (error instanceof BackendHttpError) {
				return fail(error.status, {
					profileMessage: error.message,
					profileValues: { displayName }
				});
			}

			throw error;
		}
	},

	becomeSeller: async ({ request, locals, fetch }: import('./$types').RequestEvent) => {
		if (!locals.accessToken) {
			return fail(401, { sellerMessage: 'Unauthorized' });
		}

		const formData = await request.formData();
		const publicName = String(formData.get('publicName') ?? '').trim();

		if (!publicName) {
			return fail(400, {
				sellerMessage: 'Public name is required.',
				sellerValues: { publicName }
			});
		}

		try {
			const user = await becomeSeller(fetch, locals.accessToken, { publicName });

			locals.session = {
				isAuthenticated: true,
				user
			};

			return {
				sellerSuccess: true,
				sellerMessage: 'Seller access enabled successfully.',
				sellerValues: { publicName }
			};
		} catch (error) {
			if (error instanceof BackendHttpError) {
				return fail(error.status, {
					sellerMessage: error.message,
					sellerValues: { publicName }
				});
			}

			throw error;
		}
	}
};;null as any as Actions;