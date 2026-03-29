import type { PageServerLoad } from './$types';
import { getPurchaseHistory } from '$lib/server/buyer';

export const load: PageServerLoad = async ({ locals, fetch }) => {
	const items = locals.accessToken
		? await getPurchaseHistory(fetch, locals.accessToken)
		: [];

	return {
		items
	};
};