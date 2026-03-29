<script lang="ts">
	import type { ActionData, PageData } from './$types';
	import { formatMoney, formatDateTime } from '$lib/utils/format';

	let { data, form }: { data: PageData; form: ActionData } = $props();

	const item = $derived(form?.item ?? data.item);
</script>

{#if item}
	<div class="space-y-8">
		<div class="flex flex-wrap items-start justify-between gap-5">
			<div>
				<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">{item.slug}</h2>
				<p class="mt-2 text-sm leading-6 text-neutral-600">
					Seller: {item.sellerPublicName}
				</p>
			</div>

			<div class="text-right text-sm">
				<p><span class="font-semibold">State:</span> {item.state}</p>
				<p><span class="font-semibold">Live Revision:</span> {item.currentLiveRevisionId ?? '—'}</p>
			</div>
		</div>

		<form method="POST" class="space-y-4 border border-neutral-200 p-5">
			<div class="space-y-2">
				<label class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400" for="state">
					Product State
				</label>
				<select
					id="state"
					name="state"
					class="w-full rounded-[6px] border border-neutral-400 px-4 py-3 outline-none transition focus:border-black md:max-w-[280px]"
				>
					<option value="ACTIVE" selected={(form?.values?.state ?? item.state) === 'ACTIVE'}>
						ACTIVE
					</option>
					<option value="ARCHIVED" selected={(form?.values?.state ?? item.state) === 'ARCHIVED'}>
						ARCHIVED
					</option>
				</select>
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
					Save State
				</button>
			</div>
		</form>

		<div class="space-y-5">
			<h3 class="text-2xl font-black tracking-tight">Revisions</h3>

			{#each item.revisions as revision}
				<div class="space-y-5 border border-neutral-200 p-5">
					<div class="flex flex-wrap items-start justify-between gap-5">
						<div>
							<h4 class="text-xl font-black tracking-tight">
								Revision #{revision.revisionNumber} · {revision.title}
							</h4>
							<p class="mt-2 text-sm text-neutral-600">{revision.shortDescription}</p>
						</div>

						<div class="text-right text-sm">
							<p><span class="font-semibold">Status:</span> {revision.status}</p>
							<p><span class="font-semibold">Price:</span> {formatMoney(revision.priceMinor, revision.currency)}</p>
							<p>
								<span class="font-semibold">Submitted:</span>
								{revision.submittedAt ? formatDateTime(revision.submittedAt) : '—'}
							</p>
						</div>
					</div>

					<div class="grid gap-5 md:grid-cols-2">
						<div class="space-y-2 text-sm">
							<p><span class="font-semibold">Archive:</span> {revision.archiveAttached ? 'Yes' : 'No'}</p>
							{#if revision.archiveAttached && revision.archiveUrl}
								<a
									href={revision.archiveUrl}
									target="_blank"
									rel="noreferrer"
									class="inline-flex rounded-[6px] border border-black px-4 py-2 font-bold transition hover:bg-neutral-100"
								>
									Open Archive
								</a>
							{/if}
						</div>

						{#if revision.rejectionReason}
							<div class="bg-red-50 px-4 py-3 text-sm text-red-700">
								<strong>Rejection reason:</strong> {revision.rejectionReason}
							</div>
						{/if}
					</div>

					{#if revision.images.length > 0}
						<div class="grid gap-5 md:grid-cols-2 xl:grid-cols-4">
							{#each revision.images as image}
								<div class="space-y-3 border border-neutral-200 p-3">
									<div class="aspect-square overflow-hidden bg-neutral-100">
										<img src={image.url} alt={image.originalFilename} class="h-full w-full object-cover" />
									</div>

									<div class="text-sm">
										<p class="font-bold">{image.originalFilename}</p>
										<p class="text-neutral-500">
											{image.cover ? 'Cover image' : 'Image'} · {image.mimeType}
										</p>
									</div>
								</div>
							{/each}
						</div>
					{/if}
				</div>
			{/each}
		</div>
	</div>
{/if}