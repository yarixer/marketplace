<script lang="ts">
	import type { ActionData, PageData } from './$types';
	import { formatMoney } from '$lib/utils/format';

	let { data, form }: { data: PageData; form: ActionData } = $props();

	const item = $derived(form?.item ?? data.item);
	const selectedRoles = $derived(
		form?.values?.roles ?? (item ? item.roles : [])
	);
</script>

{#if item}
	<div class="space-y-8">
		<div class="flex flex-wrap items-start justify-between gap-5">
			<div>
				<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">{item.displayName}</h2>
				<p class="mt-2 text-sm leading-6 text-neutral-600">{item.email}</p>
			</div>

			<div class="text-right text-sm">
				<p><span class="font-semibold">Enabled:</span> {item.enabled ? 'Yes' : 'No'}</p>
				<p><span class="font-semibold">Roles:</span> {item.roles.join(', ')}</p>
				<p>
					<span class="font-semibold">Wallet:</span>
					{formatMoney(item.wallet.availableMinor, item.wallet.currency)}
				</p>
			</div>
		</div>

		<form method="POST" class="space-y-6 border border-neutral-200 p-5">
			<div class="grid gap-5 md:grid-cols-2">
				<div class="space-y-2">
					<label class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400" for="displayName">
						Display Name
					</label>
					<input
						id="displayName"
						name="displayName"
						type="text"
						required
						value={form?.values?.displayName ?? item.displayName}
						class="w-full rounded-[6px] border border-neutral-400 px-4 py-3 outline-none transition focus:border-black"
					/>
				</div>

				<fieldset class="space-y-2">
					<legend class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400">
						Status
					</legend>

					<label class="inline-flex items-center gap-3 rounded-[8px] border border-neutral-300 px-4 py-3 text-sm font-medium">
						<input
							type="checkbox"
							name="enabled"
							checked={form?.values?.enabled ?? item.enabled}
							class="h-4 w-4"
						/>
						<span>Enabled</span>
					</label>
				</fieldset>
			</div>

			<fieldset class="space-y-3">
				<legend class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400">Roles</legend>

				<div class="flex flex-wrap gap-3">
					{#each ['BUYER', 'SELLER', 'ADMIN'] as role}
						<label class="inline-flex items-center gap-2 rounded-[8px] border border-neutral-300 px-4 py-3 text-sm font-medium">
							<input
								type="checkbox"
								name="roles"
								value={role}
								checked={selectedRoles.includes(role)}
								class="h-4 w-4"
							/>
							<span>{role}</span>
						</label>
					{/each}
				</div>
			</fieldset>

			{#if item.sellerProfile}
				<div class="border border-neutral-200 bg-neutral-50 p-4 text-sm">
					<p class="font-semibold">Seller profile</p>
					<p class="mt-1">Public Name: {item.sellerProfile.publicName}</p>
					<p>Slug: {item.sellerProfile.slug}</p>
				</div>
			{/if}

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
					Save User
				</button>
			</div>
		</form>
	</div>
{/if}