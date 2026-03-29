<script lang="ts">
	import type { SessionState } from '$lib/types/api';
	import HeaderSearchBar from '$lib/components/layout/HeaderSearchBar.svelte';
	import AccountDropdown from '$lib/components/layout/AccountDropdown.svelte';

	let { session }: { session: SessionState } = $props();

	let visible = $state(true);
	let lastY = 0;

	if (typeof window !== 'undefined') {
		const onScroll = () => {
			const currentY = window.scrollY;

			if (currentY <= 10) {
				visible = true;
			} else if (currentY > lastY) {
				visible = false;
			} else if (currentY < lastY) {
				visible = true;
			}

			lastY = currentY;
		};

		window.addEventListener('scroll', onScroll);
	}
</script>

<header
	class={`sticky top-0 z-30 border-b border-black bg-white/95 backdrop-blur transition-transform duration-200 ${
		visible ? 'translate-y-0' : '-translate-y-full'
	}`}
>
	<div class="mx-auto flex max-w-[1600px] items-center gap-6 px-6 py-3">
		<a href="/" class="flex shrink-0 items-center">
			<img src="/logo.webp" alt="Models" class="h-10 w-auto object-contain" />
		</a>

		<div class="min-w-0 flex-1">
			<HeaderSearchBar />
		</div>

		<div class="flex shrink-0 items-center gap-6">
			<a
				href="/products"
				class="flex h-[40px] items-center gap-2 rounded-[8px] bg-[#f5b400] px-5 text-[14px] font-extrabold text-black transition hover:bg-[#e4a700]"
			>
				<span>Products</span>
				<svg viewBox="0 0 24 24" class="h-4 w-4 fill-none stroke-current stroke-2">
					<path d="M5 12h14" />
					<path d="M13 5l7 7-7 7" />
				</svg>
			</a>

			<AccountDropdown {session} />
		</div>
	</div>
</header>