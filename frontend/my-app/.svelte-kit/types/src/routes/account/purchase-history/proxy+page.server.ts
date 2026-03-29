// @ts-nocheck
import type { PageServerLoad } from './$types';
import { getPurchaseHistory } from '$lib/server/buyer';

export const load = async ({ locals, fetch }: Parameters<PageServerLoad>[0]) => {
	const items = locals.accessToken
		? await getPurchaseHistory(fetch, locals.accessToken)
		: [];

	return {
		items
	};
};