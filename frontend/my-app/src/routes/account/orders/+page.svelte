<script lang="ts">
	import type { PageData } from './$types';
	import { formatDateTime, formatMoney } from '$lib/utils/format';

	let { data }: { data: PageData } = $props();

	function getStatusTone(status: string) {
		switch (status) {
			case 'PAID':
				return 'bg-green-50 text-green-700';
			case 'DRAFT':
				return 'bg-amber-50 text-amber-700';
			default:
				return 'bg-neutral-100 text-neutral-700';
		}
	}
</script>

<div class="space-y-8">
	<div>
		<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">Orders</h2>
		<p class="mt-2 text-sm leading-6 text-neutral-600">
			View all order records linked to your account.
		</p>
	</div>

	{#if data.items.length > 0}
		<div class="space-y-5">
			{#each data.items as order}
				<div class="border border-neutral-200 bg-white">
					<div class="flex flex-wrap items-center justify-between gap-4 border-b border-neutral-200 px-5 py-4">
						<div class="space-y-1">
							<p class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400">
								Order
							</p>
							<p class="text-lg font-black">#{order.orderId}</p>
						</div>

						<div class="flex flex-wrap items-center gap-5 text-sm">
							<div>
								<p class="text-xs uppercase tracking-[0.12em] text-neutral-400">Created</p>
								<p>{formatDateTime(order.createdAt)}</p>
							</div>

							<div>
								<p class="text-xs uppercase tracking-[0.12em] text-neutral-400">Paid</p>
								<p>{order.paidAt ? formatDateTime(order.paidAt) : '—'}</p>
							</div>

							<div>
								<p class="text-xs uppercase tracking-[0.12em] text-neutral-400">Subtotal</p>
								<p class="font-bold">{formatMoney(order.subtotalMinor, order.currency)}</p>
							</div>

							<div>
								<span class={`inline-flex px-3 py-2 text-xs font-bold ${getStatusTone(order.status)}`}>
									{order.status}
								</span>
							</div>
						</div>
					</div>

					<div class="divide-y divide-neutral-200">
						{#each order.items as item}
							<div class="flex flex-wrap items-center justify-between gap-4 px-5 py-4">
								<div>
									<a href={`/products/${item.slug}`} class="text-base font-bold hover:underline">
										{item.title}
									</a>
									<p class="mt-1 text-sm text-neutral-600">
										By {item.sellerPublicName}
									</p>
								</div>

								<div class="text-right">
									<p class="text-sm text-neutral-500">Revision #{item.revisionId}</p>
									<p class="text-lg font-black">{formatMoney(item.priceMinor, item.currency)}</p>
								</div>
							</div>
						{/each}
					</div>
				</div>
			{/each}
		</div>
	{:else}
		<div class="border border-dashed border-neutral-300 px-8 py-16 text-center">
			<h2 class="text-xl font-bold">No orders yet</h2>
			<p class="mt-2 text-sm text-neutral-600">
				Your orders will appear here after purchase.
			</p>
		</div>
	{/if}
</div>