import { fail, redirect } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';
import { BackendHttpError } from '$lib/server/backend';
import {
	approveAdminPendingRevision,
	getAdminPendingRevision,
	rejectAdminPendingRevision
} from '$lib/server/admin';

export const load: PageServerLoad = async ({ locals, fetch, params }) => {
	const revisionId = Number(params.revisionId);

	const item = locals.accessToken
		? await getAdminPendingRevision(fetch, locals.accessToken, revisionId)
		: null;

	return {
		item
	};
};

export const actions: Actions = {
	approve: async ({ locals, fetch, params }) => {
		if (!locals.accessToken) {
			return fail(401, { message: 'Unauthorized' });
		}

		const revisionId = Number(params.revisionId);

		try {
			await approveAdminPendingRevision(fetch, locals.accessToken, revisionId);
			throw redirect(303, '/admin/moderation');
		} catch (error) {
			if (error instanceof BackendHttpError) {
				return fail(error.status, { message: error.message });
			}

			throw error;
		}
	},

	reject: async ({ request, locals, fetch, params }) => {
		if (!locals.accessToken) {
			return fail(401, { rejectMessage: 'Unauthorized' });
		}

		const revisionId = Number(params.revisionId);
		const formData = await request.formData();
		const reason = String(formData.get('reason') ?? '').trim();

		if (!reason) {
			return fail(400, {
				rejectMessage: 'Rejection reason is required.',
				values: { reason }
			});
		}

		try {
			await rejectAdminPendingRevision(fetch, locals.accessToken, revisionId, {
				rejectionReason: reason
			});
			throw redirect(303, '/admin/moderation');
		} catch (error) {
			if (error instanceof BackendHttpError) {
				return fail(error.status, {
					rejectMessage: error.message,
					values: { reason }
				});
			}

			throw error;
		}
	}
};