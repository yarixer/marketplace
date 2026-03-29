import type { PageServerLoad } from './$types';
import { getAdminProducts } from '$lib/server/admin';

export const load: PageServerLoad = async ({ locals, fetch }) => {
	const items = locals.accessToken ? await getAdminProducts(fetch, locals.accessToken) : [];

	return {
		items
	};
};