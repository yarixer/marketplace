// @ts-nocheck
import { redirect } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';

export const load = async () => {
	throw redirect(302, '/account/general');
};;null as any as PageServerLoad;