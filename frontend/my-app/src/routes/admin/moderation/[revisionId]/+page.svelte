<script lang="ts">
	import type { ActionData, PageData } from './$types';
	import { formatMoney, formatDateTime } from '$lib/utils/format';

	let { data, form }: { data: PageData; form: ActionData } = $props();

	function getRejectReason(form: ActionData | undefined) {
		if (!form || !('values' in form) || !form.values) {
			return '';
		}

		const values = form.values as { reason?: string };
		return values.reason ?? '';
	}

	const rejectReason = $derived(getRejectReason(form));
	const item = $derived(data.item);
	const publicProductHref = $derived(item ? `/products/${item.productSlug}` : '#');
	const adminProductHref = $derived(item ? `/admin/products/${item.productId}` : '#');
</script>

{#if item}
	<div class="space-y-8">
		<div class="flex flex-wrap items-start justify-between gap-5">
			<div>
				<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">{item.title}</h2>
				<p class="mt-2 text-sm leading-6 text-neutral-600">
					Revision #{item.revisionNumber} · {item.productSlug}
				</p>
			</div>

			<div class="text-right text-sm">
				<p><span class="font-semibold">Seller:</span> {item.sellerPublicName}</p>
				<p><span class="font-semibold">Status:</span> {item.status}</p>
				<p><span class="font-semibold">Product State:</span> {item.productState}</p>
				<p>
					<span class="font-semibold">Submitted:</span>
					{item.submittedAt ? formatDateTime(item.submittedAt) : '—'}
				</p>
			</div>
		</div>

		<div class="grid gap-6 xl:grid-cols-[minmax(0,1.15fr)_360px]">
			<section class="space-y-6">
				<div class="border border-neutral-200 bg-white p-6 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
					<h3 class="text-3xl font-black tracking-tight text-[#1f1f1f]">Revision Details</h3>

					<div class="mt-6 space-y-6">
						<div>
							<p class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400">
								Short Description
							</p>
							<p class="mt-2 text-sm leading-7 text-neutral-700">{item.shortDescription}</p>
						</div>

						<div>
							<p class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400">
								Description
							</p>
							<div class="mt-2 whitespace-pre-line text-sm leading-8 text-neutral-700">
								{item.description}
							</div>
						</div>

						<div class="grid gap-5 md:grid-cols-2">
							<div>
								<p class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400">
									Price
								</p>
								<p class="mt-2 text-lg font-black text-[#ff4646]">
									{formatMoney(item.priceMinor, item.currency)}
								</p>
							</div>

							<div>
								<p class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400">
									Archive
								</p>
								<p class="mt-2 text-sm font-semibold text-neutral-800">
									{item.archiveAttached ? 'Attached' : 'Not attached'}
								</p>
							</div>
						</div>

						<div>
							<p class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400">
								Tags
							</p>

							{#if item.tags.length > 0}
								<div class="mt-3 flex flex-wrap gap-2">
									{#each item.tags as tag}
										<span class="rounded-[8px] border border-neutral-300 px-3 py-1 text-xs font-semibold uppercase tracking-[0.08em] text-neutral-700">
											{tag.name}
										</span>
									{/each}
								</div>
							{:else}
								<p class="mt-2 text-sm text-neutral-500">No tags.</p>
							{/if}
						</div>

						{#if item.currentLiveRevisionId}
							<div>
								<p class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400">
									Current Live Revision
								</p>
								<p class="mt-2 text-sm font-semibold text-neutral-800">
									#{item.currentLiveRevisionId}
								</p>
							</div>
						{/if}

						{#if item.reviewedAt}
							<div>
								<p class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400">
									Last Reviewed
								</p>
								<p class="mt-2 text-sm text-neutral-700">
									{formatDateTime(item.reviewedAt)}
								</p>
							</div>
						{/if}

						{#if item.rejectionReason}
							<div class="rounded-[8px] bg-red-50 px-4 py-4 text-sm leading-7 text-red-700">
								<strong>Previous rejection reason:</strong> {item.rejectionReason}
							</div>
						{/if}
					</div>
				</div>

				<div class="border border-neutral-200 bg-white p-6 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
					<h3 class="text-2xl font-black tracking-tight text-[#1f1f1f]">Available Admin Actions</h3>

					<div class="mt-5 flex flex-wrap gap-3">
						<a
							href={publicProductHref}
							target="_blank"
							rel="noreferrer"
							class="rounded-[6px] border border-black px-5 py-3 text-sm font-bold transition hover:bg-neutral-100"
						>
							Open Public Product
						</a>

						<a
							href={adminProductHref}
							class="rounded-[6px] border border-black px-5 py-3 text-sm font-bold transition hover:bg-neutral-100"
						>
							Open Admin Product
						</a>
					</div>
				</div>

				<div class="border border-neutral-200 bg-amber-50 p-6 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
					<h3 class="text-2xl font-black tracking-tight text-[#1f1f1f]">Files Preview</h3>
					<p class="mt-3 text-sm leading-7 text-neutral-700">
						The current admin API does not expose archive download links or revision preview images.
						You can still review metadata, price, tags, product state and then approve or reject.
					</p>
				</div>
			</section>

			<aside class="space-y-5 xl:sticky xl:top-24 xl:self-start">
				<form method="POST" action="?/approve" class="border border-neutral-200 bg-green-50 p-5 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
					<h3 class="text-xl font-black tracking-tight text-[#1f1f1f]">Approve</h3>
					<p class="mt-2 text-sm leading-6 text-neutral-700">
						Approve this revision and make it live.
					</p>

					{#if form?.message}
						<p class="mt-4 bg-red-50 px-4 py-3 text-sm text-red-700">{form.message}</p>
					{/if}

					<div class="mt-5">
						<button
							type="submit"
							class="w-full rounded-[6px] bg-green-600 px-5 py-3 text-sm font-bold text-white transition hover:bg-green-700"
						>
							Approve Revision
						</button>
					</div>
				</form>

				<form method="POST" action="?/reject" class="border border-neutral-200 bg-red-50 p-5 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
					<h3 class="text-xl font-black tracking-tight text-[#1f1f1f]">Reject</h3>
					<p class="mt-2 text-sm leading-6 text-neutral-700">
						Reject this revision and send it back to the seller.
					</p>

					<div class="mt-4 space-y-2">
						<label class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-500" for="reason">
							Rejection reason
						</label>
						<textarea
							id="reason"
							name="reason"
							rows="6"
							class="w-full rounded-[6px] border border-neutral-400 px-4 py-3 outline-none transition focus:border-black"
						>{rejectReason}</textarea>
					</div>

					{#if form?.rejectMessage}
						<p class="mt-4 bg-red-100 px-4 py-3 text-sm text-red-700">{form.rejectMessage}</p>
					{/if}

					<div class="mt-5">
						<button
							type="submit"
							class="w-full rounded-[6px] bg-[#ff4646] px-5 py-3 text-sm font-bold text-white transition hover:bg-[#f03a3a]"
						>
							Reject Revision
						</button>
					</div>
				</form>

				<div class="border border-neutral-200 bg-white p-5 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
					<h3 class="text-xl font-black tracking-tight text-[#1f1f1f]">Moderation Summary</h3>

					<div class="mt-4 space-y-3 text-sm">
						<div class="flex items-start justify-between gap-4">
							<span class="text-neutral-500">Revision</span>
							<span class="text-right font-semibold text-neutral-800">#{item.revisionNumber}</span>
						</div>

						<div class="flex items-start justify-between gap-4">
							<span class="text-neutral-500">Seller</span>
							<span class="text-right font-semibold text-neutral-800">{item.sellerPublicName}</span>
						</div>

						<div class="flex items-start justify-between gap-4">
							<span class="text-neutral-500">State</span>
							<span class="text-right font-semibold text-neutral-800">{item.productState}</span>
						</div>

						<div class="flex items-start justify-between gap-4">
							<span class="text-neutral-500">Archive</span>
							<span class="text-right font-semibold text-neutral-800">
								{item.archiveAttached ? 'Yes' : 'No'}
							</span>
						</div>
					</div>
				</div>
			</aside>
		</div>
	</div>
{/if}