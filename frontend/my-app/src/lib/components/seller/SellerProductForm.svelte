<script lang="ts">
	import type { PublicProductTagResponse } from '$lib/types/public';

	type FormValues = {
		title: string;
		shortDescription: string;
		description: string;
		priceUsd: string;
		selectedTagSlugs: string[];
	};

	let {
		action = '?/updateDraft',
		submitLabel,
		values,
		availableTags,
		message = null,
		success = false
	}: {
		action?: string;
		submitLabel: string;
		values: FormValues;
		availableTags: PublicProductTagResponse[];
		message?: string | null;
		success?: boolean;
	} = $props();
</script>

<form method="POST" action={action} class="border border-neutral-200 bg-white p-6 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
	<div class="space-y-2">
		<h3 class="text-3xl font-black tracking-tight text-[#1f1f1f]">Product Details</h3>
		<p class="text-sm leading-6 text-neutral-600">
			Update title, description, price and tags. Files stay attached until you replace or delete them.
		</p>
	</div>

	<div class="mt-6 grid gap-6">
		<div class="space-y-2">
			<label class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400" for="title">
				Title
			</label>
			<input
				id="title"
				name="title"
				type="text"
				required
				value={values.title}
				class="w-full rounded-[6px] border border-neutral-300 px-4 py-3 outline-none transition focus:border-black"
			/>
		</div>

		<div class="space-y-2">
			<label
				class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400"
				for="shortDescription"
			>
				Short Description
			</label>
			<input
				id="shortDescription"
				name="shortDescription"
				type="text"
				required
				value={values.shortDescription}
				class="w-full rounded-[6px] border border-neutral-300 px-4 py-3 outline-none transition focus:border-black"
			/>
		</div>

		<div class="space-y-2">
			<label
				class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400"
				for="description"
			>
				Description
			</label>
			<textarea
				id="description"
				name="description"
				rows="10"
				required
				class="w-full rounded-[6px] border border-neutral-300 px-4 py-3 outline-none transition focus:border-black"
			>{values.description}</textarea>
		</div>

		<div class="grid gap-6 md:grid-cols-[240px_minmax(0,1fr)]">
			<div class="space-y-2">
				<label
					class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400"
					for="priceUsd"
				>
					Price (USD)
				</label>
				<input
					id="priceUsd"
					name="priceUsd"
					type="text"
					inputmode="decimal"
					placeholder="12.99"
					required
					value={values.priceUsd}
					class="w-full rounded-[6px] border border-neutral-300 px-4 py-3 outline-none transition focus:border-black"
				/>
				<p class="text-xs text-neutral-500">Use dollars, e.g. 4.99 or 12.50</p>
			</div>

			<fieldset class="space-y-3">
				<legend class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400">
					Tags
				</legend>

				<div class="flex flex-wrap gap-3">
					{#each availableTags as tag}
						<label
							class="inline-flex cursor-pointer items-center gap-2 rounded-[8px] border border-neutral-300 bg-white px-3 py-2 text-sm font-medium transition hover:bg-neutral-50"
						>
							<input
								type="checkbox"
								name="tagSlugs"
								value={tag.slug}
								checked={values.selectedTagSlugs.includes(tag.slug)}
								class="h-4 w-4 rounded border-neutral-400"
							/>
							<span>{tag.name}</span>
						</label>
					{/each}
				</div>
			</fieldset>
		</div>
	</div>

	{#if message}
		<p class={`mt-6 px-4 py-3 text-sm ${success ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>
			{message}
		</p>
	{/if}

	<div class="mt-6 flex justify-end border-t border-neutral-200 pt-5">
		<button
			type="submit"
			class="rounded-[6px] bg-[#ff4646] px-6 py-3 text-sm font-bold text-white transition hover:bg-[#f03a3a]"
		>
			{submitLabel}
		</button>
	</div>
</form>