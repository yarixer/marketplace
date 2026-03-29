<script lang="ts">
	import type { SellerDashboardResponse } from '$lib/types/api';
	import { formatMoney } from '$lib/utils/format';

	let {
		dashboard,
		pathname
	}: {
		dashboard: SellerDashboardResponse;
		pathname: string;
	} = $props();

	const links = [
		{ href: '/seller/dashboard', label: 'Dashboard' },
		{ href: '/seller/products', label: 'Products' },
		{ href: '/seller/stats', label: 'Stats' },
		{ href: '/seller/settings', label: 'Settings' }
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
		<p class="text-sm font-black">
			{formatMoney(dashboard.wallet.availableMinor, dashboard.wallet.currency)}
		</p>
	</div>
</div>