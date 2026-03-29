<script lang="ts">
	import type { SellerProductDetailsResponse, SellerProductRevisionResponse } from '$lib/types/api';
	import { formatMoney } from '$lib/utils/format';

	let {
		product,
		submitMessage = null,
		submitSuccess = false
	}: {
		product: SellerProductDetailsResponse;
		submitMessage?: string | null;
		submitSuccess?: boolean;
	} = $props();

	const draft = $derived(product.draftRevision);
	const pending = $derived(product.pendingRevision);
	const live = $derived(product.liveRevision);

	function ratingLabel(score: number) {
		if (score === 0) return 'No rating';
		if (score > 0) return `Positive (${score})`;
		return `Negative (${Math.abs(score)})`;
	}

	function priceText(revision: SellerProductRevisionResponse | null) {
		if (!revision) return '—';
		return formatMoney(revision.priceMinor, revision.currency);
	}
</script>

<div class="space-y-5 xl:sticky xl:top-24 xl:self-start">
	<div class="border border-neutral-200 bg-white p-5 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
		<h3 class="text-xl font-black tracking-tight text-[#1f1f1f]">Workflow</h3>

		<div class="mt-4 space-y-3 text-sm">
			<div class="flex items-start justify-between gap-4">
				<span class="text-neutral-500">State</span>
				<span class="text-right font-semibold text-neutral-800">{product.state}</span>
			</div>

			<div class="flex items-start justify-between gap-4">
				<span class="text-neutral-500">Draft</span>
				<span class="text-right font-semibold text-neutral-800">
					{draft ? `#${draft.revisionNumber}` : 'None'}
				</span>
			</div>

			<div class="flex items-start justify-between gap-4">
				<span class="text-neutral-500">Pending</span>
				<span class="text-right font-semibold text-neutral-800">
					{pending ? `#${pending.revisionNumber}` : 'None'}
				</span>
			</div>

			<div class="flex items-start justify-between gap-4">
				<span class="text-neutral-500">Live</span>
				<span class="text-right font-semibold text-neutral-800">
					{live ? `#${live.revisionNumber}` : 'None'}
				</span>
			</div>

			<div class="flex items-start justify-between gap-4">
				<span class="text-neutral-500">Current Price</span>
				<span class="text-right font-semibold text-neutral-800">
					{priceText(draft ?? live)}
				</span>
			</div>

			<div class="flex items-start justify-between gap-4">
				<span class="text-neutral-500">Rating</span>
				<span class="text-right font-semibold text-neutral-800">
					{ratingLabel(product.voteSummary.score)}
				</span>
			</div>
		</div>
	</div>

	<div class="border border-neutral-200 bg-white p-5 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
		<h3 class="text-xl font-black tracking-tight text-[#1f1f1f]">Submit For Review</h3>
		<p class="mt-2 text-sm leading-6 text-neutral-600">
			You can keep at most 2 revisions pending review at the same time.
		</p>

		<form method="POST" action="?/submitReview" class="mt-5">
			<button
				type="submit"
				class="w-full rounded-[6px] bg-[#ff4646] px-5 py-3 text-sm font-bold text-white transition hover:bg-[#f03a3a]"
			>
				Submit Revision
			</button>
		</form>

		{#if submitMessage}
			<p class={`mt-4 px-4 py-3 text-sm ${submitSuccess ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>
				{submitMessage}
			</p>
		{/if}
	</div>

	<div class="border border-neutral-200 bg-neutral-50 p-5 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
		<h3 class="text-lg font-black tracking-tight text-[#1f1f1f]">Notes</h3>
		<ul class="mt-3 space-y-2 text-sm leading-6 text-neutral-600">
			<li>• Text changes are saved only when you press “Save Draft”.</li>
			<li>• Archive upload starts immediately after file selection.</li>
			<li>• Image upload starts immediately after file selection.</li>
			<li>• Current cover image is shown but cannot be changed manually in this version.</li>
		</ul>
	</div>
</div>