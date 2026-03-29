<script lang="ts">
	import type { PublicProductTagResponse } from '$lib/types/public';

	let {
		tags,
		selectedTags,
		currentSort,
		currentQuery
	}: {
		tags: PublicProductTagResponse[];
		selectedTags: string[];
		currentSort: string;
		currentQuery: string;
	} = $props();
</script>

<form method="GET" action="/products" class="bg-white">
	<input type="hidden" name="q" value={currentQuery} />
	<input type="hidden" name="sort" value={currentSort} />

	<div class="space-y-4">
		<div class="space-y-3">
			<p class="text-sm font-semibold">Tags</p>

			<div class="flex flex-col gap-2">
				{#each tags as tag}
					<label class="flex items-center gap-3 text-sm">
						<input
							type="checkbox"
							name="tags"
							value={tag.slug}
							checked={selectedTags.includes(tag.slug)}
							class="h-4 w-4 rounded border-neutral-400"
						/>
						<span>{tag.name}</span>
					</label>
				{/each}
			</div>
		</div>

		<button
			type="submit"
			class="w-full bg-neutral-950 px-4 py-3 text-sm font-semibold text-white transition hover:bg-neutral-800"
		>
			Apply
		</button>
	</div>
</form>