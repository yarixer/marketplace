<script lang="ts">
	import type { SellerMonthlySalesPointResponse } from '$lib/types/api';
	import { formatMoney } from '$lib/utils/format';

	let {
		items,
		currency
	}: {
		items: SellerMonthlySalesPointResponse[];
		currency: string;
	} = $props();

	const maxRevenue = $derived(Math.max(...items.map((item) => item.grossRevenueMinor), 1));

	function getHeight(value: number) {
		return Math.max(8, Math.round((value / maxRevenue) * 180));
	}
</script>

<div class="border border-neutral-200 bg-white p-5">
	<div class="mb-6">
		<h2 class="text-2xl font-black tracking-tight">Sales last 12 months</h2>
		<p class="mt-2 text-sm text-neutral-600">
			Gross revenue by month.
		</p>
	</div>

	<div class="grid grid-cols-12 items-end gap-3">
		{#each items as item}
			<div class="flex flex-col items-center gap-2">
				<div class="flex h-[220px] items-end">
					<div
						class="w-10 bg-[#ff4646] transition"
						style={`height:${getHeight(item.grossRevenueMinor)}px`}
						title={`${item.month} · ${formatMoney(item.grossRevenueMinor, currency)} · ${item.salesCount} sales`}
					></div>
				</div>

				<div class="text-center">
					<p class="text-[11px] font-bold">{item.month.slice(5)}</p>
					<p class="text-[10px] text-neutral-500">{item.salesCount}</p>
				</div>
			</div>
		{/each}
	</div>
</div>