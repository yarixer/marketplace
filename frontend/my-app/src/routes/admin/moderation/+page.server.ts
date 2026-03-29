import type { PageServerLoad } from './$types';
import { getAdminPendingRevisions } from '$lib/server/admin';

export const load: PageServerLoad = async ({ locals, fetch }) => {
	const items = locals.accessToken ? await getAdminPendingRevisions(fetch, locals.accessToken) : [];

	return {
		items
	};
};