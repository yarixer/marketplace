import { fail } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';
import { BackendHttpError } from '$lib/server/backend';
import { getAdminProduct, updateAdminProductState } from '$lib/server/admin';

export const load: PageServerLoad = async ({ locals, fetch, params }) => {
	const productId = Number(params.productId);

	const item = locals.accessToken ? await getAdminProduct(fetch, locals.accessToken, productId) : null;

	return {
		item
	};
};

export const actions: Actions = {
	default: async ({ request, locals, fetch, params }) => {
		if (!locals.accessToken) {
			return fail(401, {
				message: 'Unauthorized',
				values: { state: 'ACTIVE' as 'ACTIVE' | 'ARCHIVED' }
			});
		}

		const productId = Number(params.productId);
		const formData = await request.formData();
		const state = String(formData.get('state') ?? 'ACTIVE') as 'ACTIVE' | 'ARCHIVED';

		try {
			const item = await updateAdminProductState(fetch, locals.accessToken, productId, state);

			return {
				success: true,
				message: 'Product state updated successfully.',
				item,
				values: { state }
			};
		} catch (error) {
			if (error instanceof BackendHttpError) {
				return fail(error.status, {
					message: error.message,
					values: { state }
				});
			}

			throw error;
		}
	}
};