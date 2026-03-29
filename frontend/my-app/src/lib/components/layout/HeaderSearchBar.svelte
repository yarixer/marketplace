<script lang="ts">
	let { initialQuery = '' }: { initialQuery?: string } = $props();
</script>

<form method="GET" action="/products" class="w-full">
	<div class="flex h-[46px] w-full items-stretch rounded-[8px] border border-black bg-white p-[3px]">
		<input
			type="text"
			name="q"
			value={initialQuery}
			placeholder="Search for models, textures, plugins and more..."
			class="min-w-0 flex-1 rounded-[6px] border-0 bg-transparent px-4 text-[15px] text-black outline-none placeholder:text-[#b7aea8]"
		/>

		<button
			type="submit"
			class="rounded-[6px] bg-red-600 px-5 text-[14px] font-extrabold uppercase tracking-[0.02em] text-white transition hover:bg-red-700"
		>
			Search
		</button>
	</div>
</form>