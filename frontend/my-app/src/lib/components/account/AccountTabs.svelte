<script lang="ts">
	import type { AuthUserResponse, WalletBalanceResponse } from '$lib/types/api';
	import { formatMoney } from '$lib/utils/format';

	let {
		user: _user,
		wallet,
		pathname
	}: {
		user: AuthUserResponse;
		wallet: WalletBalanceResponse;
		pathname: string;
	} = $props();

	const links = [
		{ href: '/account/general', label: 'General' },
		{ href: '/account/security', label: 'Security' },
		{ href: '/account/orders', label: 'Orders' },
		{ href: '/account/library', label: 'Library' },
		{ href: '/account/purchase-history', label: 'Purchase History' }
	];

	function isActive(href: string) {
		return pathname === href;
	}
</script>

<div class="flex flex-wrap items-center justify-between gap-4 px-5 py-5 md:px-8">
	<div class="flex flex-wrap items-center gap-3">
		{#each links as link}
			<a
				href={link.href}
				class={`px-4 py-2 text-sm font-bold transition ${
					isActive(link.href)
						? 'rounded-[8px] bg-[#ff4646] text-white'
						: 'text-[#32435a] hover:text-black'
				}`}
			>
				{link.label}
			</a>
		{/each}
	</div>

	<div class="border border-neutral-200 bg-neutral-50 px-4 py-2">
		<p class="text-[10px] font-semibold uppercase tracking-[0.18em] text-neutral-500">Balance</p>
		<p class="text-sm font-black">{formatMoney(wallet.availableMinor, wallet.currency)}</p>
	</div>
</div>