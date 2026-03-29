<script lang="ts">
	let {
		page,
		totalPages,
		baseUrl
	}: {
		page: number;
		totalPages: number;
		baseUrl: string;
	} = $props();

	function pageHref(targetPage: number) {
		const url = new URL(baseUrl, 'http://local');
		url.searchParams.set('page', String(targetPage));
		return `${url.pathname}${url.search}`;
	}
</script>

{#if totalPages > 1}
	<nav class="flex items-center justify-center gap-2">
		<a
			href={page > 0 ? pageHref(page - 1) : '#'}
			aria-disabled={page <= 0}
			class="rounded-full border border-black px-4 py-2 text-sm font-medium transition hover:bg-neutral-100 aria-disabled:pointer-events-none aria-disabled:opacity-40"
		>
			Prev
		</a>

		{#each Array.from({ length: totalPages }, (_, i) => i) as pageIndex}
			<a
				href={pageHref(pageIndex)}
				class={`rounded-full px-4 py-2 text-sm font-medium transition ${
					pageIndex === page
						? 'bg-neutral-950 text-white'
						: 'border border-black hover:bg-neutral-100'
				}`}
			>
				{pageIndex + 1}
			</a>
		{/each}

		<a
			href={page < totalPages - 1 ? pageHref(page + 1) : '#'}
			aria-disabled={page >= totalPages - 1}
			class="rounded-full border border-black px-4 py-2 text-sm font-medium transition hover:bg-neutral-100 aria-disabled:pointer-events-none aria-disabled:opacity-40"
		>
			Next
		</a>
	</nav>
{/if}