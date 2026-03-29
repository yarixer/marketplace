// @ts-nocheck
import type { PageServerLoad } from './$types';
import { getSellerMonthlySales } from '$lib/server/seller';

export const load = async ({ parent, locals, fetch }: Parameters<PageServerLoad>[0]) => {
	const { dashboard } = await parent();

	const monthlySales = locals.accessToken
		? await getSellerMonthlySales(fetch, locals.accessToken)
		: [];

	return {
		dashboard,
		monthlySales
	};
};