import type { SessionState } from '$lib/types/api';

declare global {
	namespace App {
		interface Locals {
			session: SessionState;
			accessToken: string | null;
		}

		interface PageData {
			session: SessionState;
		}
	}
}

export {};