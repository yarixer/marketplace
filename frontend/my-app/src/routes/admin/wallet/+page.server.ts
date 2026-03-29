import { fail } from '@sveltejs/kit';
import type { Actions } from './$types';
import { BackendHttpError } from '$lib/server/backend';
import { manualAdminWalletCredit } from '$lib/server/admin';
import { majorStringToMinor } from '$lib/utils/money';

export const actions: Actions = {
	default: async ({ request, locals, fetch }) => {
		if (!locals.accessToken) {
			return fail(401, {
				message: 'Unauthorized',
				values: {
					userEmail: '',
					amountUsd: '',
					note: ''
				}
			});
		}

		const formData = await request.formData();

		const userEmail = String(formData.get('userEmail') ?? '').trim();
		const amountUsd = String(formData.get('amountUsd') ?? '').trim();
		const note = String(formData.get('note') ?? '').trim();

		if (!userEmail || !amountUsd || !note) {
			return fail(400, {
				message: 'User email, amount and note are required.',
				values: { userEmail, amountUsd, note }
			});
		}

		let amountMinor: number;

		try {
			amountMinor = majorStringToMinor(amountUsd);
		} catch (error) {
			return fail(400, {
				message: error instanceof Error ? error.message : 'Invalid amount.',
				values: { userEmail, amountUsd, note }
			});
		}

		try {
			const result = await manualAdminWalletCredit(fetch, locals.accessToken, {
				userEmail,
				amountMinor,
				note
			});

			return {
				success: true,
				message: `Credited ${result.targetUserEmail} successfully.`,
				values: {
					userEmail: '',
					amountUsd: '',
					note: ''
				}
			};
		} catch (error) {
			if (error instanceof BackendHttpError) {
				return fail(error.status, {
					message: error.message,
					values: { userEmail, amountUsd, note }
				});
			}

			throw error;
		}
	}
};