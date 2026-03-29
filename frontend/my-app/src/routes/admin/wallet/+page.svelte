<script lang="ts">
	import type { ActionData } from './$types';

	let { form }: { form: ActionData } = $props();
</script>

<div class="space-y-8">
	<div>
		<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">Wallet Tools</h2>
		<p class="mt-2 text-sm leading-6 text-neutral-600">
			Manual wallet credit tool for admin operations.
		</p>
	</div>

	<form method="POST" class="max-w-2xl space-y-6 border border-neutral-200 p-5">
		<div class="space-y-2">
			<label class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400" for="userEmail">
				User Email
			</label>
			<input
				id="userEmail"
				name="userEmail"
				type="email"
				required
				value={form?.values?.userEmail ?? ''}
				class="w-full rounded-[6px] border border-neutral-400 px-4 py-3 outline-none transition focus:border-black"
			/>
		</div>

		<div class="space-y-2">
			<label class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400" for="amountUsd">
				Amount (USD)
			</label>
			<input
				id="amountUsd"
				name="amountUsd"
				type="text"
				inputmode="decimal"
				placeholder="10.00"
				required
				value={form?.values?.amountUsd ?? ''}
				class="w-full rounded-[6px] border border-neutral-400 px-4 py-3 outline-none transition focus:border-black"
			/>
		</div>

		<div class="space-y-2">
			<label class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400" for="note">
				Note
			</label>
			<textarea
				id="note"
				name="note"
				rows="5"
				required
				class="w-full rounded-[6px] border border-neutral-400 px-4 py-3 outline-none transition focus:border-black"
			>{form?.values?.note ?? ''}</textarea>
		</div>

		{#if form?.message}
			<p class={`px-4 py-3 text-sm ${form.success ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>
				{form.message}
			</p>
		{/if}

		<div class="flex justify-end">
			<button
				type="submit"
				class="rounded-[6px] bg-[#ff4646] px-6 py-3 text-sm font-bold text-white transition hover:bg-[#f03a3a]"
			>
				Credit Wallet
			</button>
		</div>
	</form>
</div>