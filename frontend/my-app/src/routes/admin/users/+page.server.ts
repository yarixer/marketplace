import type { PageServerLoad } from './$types';
import { getAdminUsers } from '$lib/server/admin';

export const load: PageServerLoad = async ({ locals, fetch }) => {
	const items = locals.accessToken ? await getAdminUsers(fetch, locals.accessToken) : [];

	return {
		items
	};
};