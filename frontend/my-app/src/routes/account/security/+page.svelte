<script lang="ts">
	import type { ActionData } from './$types';

	let { form }: { form: ActionData } = $props();
</script>

<div class="space-y-8">
	<div>
		<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">Security</h2>
		<p class="mt-2 text-sm leading-6 text-neutral-600">
			Update your account password.
		</p>
	</div>

	<form method="POST" class="max-w-3xl space-y-6">
		<div class="space-y-2">
			<label class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400" for="oldPassword">
				Old Password
			</label>
			<input
				id="oldPassword"
				name="oldPassword"
				type="password"
				required
				class="w-full rounded-[6px] border border-neutral-400 px-4 py-3 outline-none transition focus:border-black"
			/>
		</div>

		<div class="grid gap-5 md:grid-cols-2">
			<div class="space-y-2">
				<label class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400" for="newPassword">
					New Password
				</label>
				<input
					id="newPassword"
					name="newPassword"
					type="password"
					required
					class="w-full rounded-[6px] border border-neutral-400 px-4 py-3 outline-none transition focus:border-black"
				/>
			</div>

			<div class="space-y-2">
				<label class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400" for="confirmNewPassword">
					Confirm New Password
				</label>
				<input
					id="confirmNewPassword"
					name="confirmNewPassword"
					type="password"
					required
					class="w-full rounded-[6px] border border-neutral-400 px-4 py-3 outline-none transition focus:border-black"
				/>
			</div>
		</div>

		{#if form?.message}
			<p
				class={`px-4 py-3 text-sm ${
					form.success ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'
				}`}
			>
				{form.message}
			</p>
		{/if}

		<div class="flex justify-end">
			<button
				type="submit"
				class="rounded-[6px] bg-[#ff4646] px-6 py-3 text-sm font-bold text-white transition hover:bg-[#f03a3a]"
			>
				Update Password
			</button>
		</div>
	</form>
</div>