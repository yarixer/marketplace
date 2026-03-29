<script lang="ts">
	import type { PageData } from './$types';
	import SellerSummaryCard from '$lib/components/seller/SellerSummaryCard.svelte';
	import MonthlySalesChart from '$lib/components/seller/MonthlySalesChart.svelte';
	import { formatMoney } from '$lib/utils/format';

	let { data }: { data: PageData } = $props();
</script>

<div class="space-y-8">
	<div>
		<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">Dashboard</h2>
		<p class="mt-2 text-sm leading-6 text-neutral-600">
			Overview of your products, sales and current balance.
		</p>
	</div>

	<div class="grid gap-5 md:grid-cols-2 xl:grid-cols-4">
		<SellerSummaryCard
			label="Available Balance"
			value={formatMoney(data.dashboard.wallet.availableMinor, data.dashboard.wallet.currency)}
			tone="green"
		/>
		<SellerSummaryCard label="Total Products" value={String(data.dashboard.totalProducts)} />
		<SellerSummaryCard label="Published Products" value={String(data.dashboard.publishedProducts)} tone="amber" />
		<SellerSummaryCard label="Pending Review" value={String(data.dashboard.pendingReviewCount)} tone="red" />
	</div>

	<div class="grid gap-5 md:grid-cols-2">
		<SellerSummaryCard label="Total Sales" value={String(data.dashboard.totalSalesCount)} />
		<SellerSummaryCard
			label="Gross Revenue"
			value={formatMoney(data.dashboard.grossRevenueMinor, data.dashboard.wallet.currency)}
		/>
	</div>

	<MonthlySalesChart
		items={data.monthlySales}
		currency={data.dashboard.wallet.currency}
	/>
</div>