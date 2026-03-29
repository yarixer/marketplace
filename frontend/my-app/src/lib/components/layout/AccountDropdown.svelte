<script lang="ts">
	import type { SessionState } from '$lib/types/api';

	let { session }: { session: SessionState } = $props();

	let open = $state(false);
	let rootElement = $state<HTMLDivElement | null>(null);

	function toggle() {
		open = !open;
	}

	function close() {
		open = false;
	}

	function handleDocumentClick(event: MouseEvent) {
		if (!open || !rootElement) return;

		const target = event.target;
		if (target instanceof Node && !rootElement.contains(target)) {
			close();
		}
	}
</script>

<svelte:document onclick={handleDocumentClick} />

<div class="relative" bind:this={rootElement}>
	<button
		type="button"
		onclick={toggle}
		class="flex h-[40px] items-center gap-2 rounded-[8px] bg-[#ff4646] px-5 text-[14px] font-extrabold text-white transition hover:bg-[#f03a3a]"
	>
		<span>Account</span>
		<svg viewBox="0 0 24 24" class="h-4 w-4 fill-none stroke-current stroke-2">
			<path d="M6 9l6 6 6-6" />
		</svg>
	</button>

	{#if open}
		<div
			class="absolute right-0 top-[calc(100%+0.5rem)] z-50 w-72 border border-black bg-white p-3 shadow-[0_20px_60px_rgba(0,0,0,0.12)]"
		>
			{#if session.isAuthenticated && session.user}
				<div class="border-b border-neutral-200 px-3 pb-3">
					<p class="text-xs uppercase tracking-[0.18em] text-neutral-500">Signed in as</p>
					<p class="mt-1 font-semibold">{session.user.displayName}</p>
				</div>

				<div class="mt-3 flex flex-col gap-2">
					<a
						href="/account/general"
						onclick={close}
						class="bg-white px-4 py-3 text-sm font-medium transition hover:bg-neutral-100"
					>
						Account settings
					</a>
					
					<a
						href="/account/library"
						onclick={close}
						class="bg-white px-4 py-3 text-sm font-medium transition hover:bg-neutral-100"
					>
						Library
					</a>
					
					{#if session.user.roles.includes('ADMIN')}
						<a
							href="/admin/moderation"
							onclick={close}
							class="bg-white px-4 py-3 text-sm font-medium transition hover:bg-neutral-100"
						>
							Admin Panel
						</a>
					{/if}
					
					<a
						href="/account/purchase-history"
						onclick={close}
						class="bg-white px-4 py-3 text-sm font-medium transition hover:bg-neutral-100"
					>
						Past orders
					</a>

					<form method="POST" action="/auth/logout?redirectTo=/">
						<button
							type="submit"
							class="w-full bg-neutral-950 px-4 py-3 text-sm font-semibold text-white transition hover:bg-neutral-800"
						>
							Log Out
						</button>
					</form>
				</div>
			{:else}
				<div class="flex flex-col gap-2">
					<a
						href="/auth/login"
						onclick={close}
						class="bg-white px-4 py-3 text-sm font-medium transition hover:bg-neutral-100"
					>
						Sign In
					</a>

					<a
						href="/auth/register"
						onclick={close}
						class="bg-white px-4 py-3 text-sm font-medium transition hover:bg-neutral-100"
					>
						Register
					</a>
				</div>
			{/if}
		</div>
	{/if}
</div>