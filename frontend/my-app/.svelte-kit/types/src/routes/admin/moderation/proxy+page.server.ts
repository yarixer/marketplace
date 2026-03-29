// @ts-nocheck
import type { PageServerLoad } from './$types';
import { getAdminPendingRevisions } from '$lib/server/admin';

export const load = async ({ locals, fetch }: Parameters<PageServerLoad>[0]) => {
	const items = locals.accessToken ? await getAdminPendingRevisions(fetch, locals.accessToken) : [];

	return {
		items
	};
};