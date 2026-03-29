export type RoleCode = 'BUYER' | 'SELLER' | 'ADMIN';

export interface SellerProfileResponse {
	publicName: string;
	slug: string;
}

export interface AuthUserResponse {
	id: number;
	email: string;
	displayName: string;
	roles: RoleCode[];
	seller: boolean;
	sellerProfile: SellerProfileResponse | null;
}

export interface AuthTokensResponse {
	accessToken: string;
	accessTokenExpiresAt: string;
	refreshToken: string;
	refreshTokenExpiresAt: string;
	user: AuthUserResponse;
}

export interface WalletBalanceResponse {
	walletAccountId: number | null;
	currency: string;
	availableMinor: number;
	pendingMinor: number;
}

export interface BuyerPurchaseHistoryRowResponse {
	orderId: number;
	orderNumber: string;
	productName: string;
	productSlug: string;
	date: string;
	status: string;
}

export interface BuyerOrderItemResponse {
	productId: number;
	slug: string;
	revisionId: number;
	title: string;
	priceMinor: number;
	currency: string;
	sellerPublicName: string;
}

export interface BuyerOrderResponse {
	orderId: number;
	status: string;
	subtotalMinor: number;
	currency: string;
	createdAt: string;
	paidAt: string | null;
	items: BuyerOrderItemResponse[];
}

export interface BuyerLibraryItemResponse {
	entitlementId: number;
	productId: number;
	currentLiveRevisionId: number | null;
	slug: string;
	title: string;
	shortDescription: string;
	priceMinor: number;
	currency: string;
	sellerPublicName: string;
	archiveAttached: boolean;
	downloadAvailable: boolean;
	grantedAt: string;
}

export interface BuyerDownloadLinkResponse {
	productId: number;
	revisionId: number;
	filename: string;
	url: string;
	expiresAt: string;
}

export interface SellerProductTagResponse {
	id: number;
	name: string;
	slug: string;
}

export interface SellerProductImageResponse {
	imageId: number;
	url: string;
	originalFilename: string;
	mimeType: string;
	sortOrder: number;
	cover: boolean;
}

export interface SellerProductRevisionResponse {
	revisionId: number;
	revisionNumber: number;
	status: string;
	title: string;
	shortDescription: string;
	description: string;
	priceMinor: number;
	currency: string;
	tags: SellerProductTagResponse[];
	archiveOriginalFilename: string | null;
	archiveSizeBytes: number | null;
	archiveAttached: boolean;
	images: SellerProductImageResponse[];
	submittedAt: string | null;
	reviewedAt: string | null;
	rejectionReason: string | null;
}

export interface PublicProductVoteSummaryResponse {
	positiveCount: number;
	negativeCount: number;
	score: number;
}

export interface SellerProductDetailsResponse {
	productId: number;
	slug: string;
	state: string;
	draftRevision: SellerProductRevisionResponse | null;
	pendingRevision: SellerProductRevisionResponse | null;
	liveRevision: SellerProductRevisionResponse | null;
	voteSummary: PublicProductVoteSummaryResponse;
}

export interface SellerProductListItemResponse {
	productId: number;
	slug: string;
	title: string;
	workflowStatus: string;
	updatedAt: string;
	ratingScore: number;
	positiveVotes: number;
	negativeVotes: number;
}

export interface SellerProductSubmitReviewResponse {
	productId: number;
	revisionId: number;
	status: string;
}

export interface SellerDashboardResponse {
	wallet: WalletBalanceResponse;
	totalProducts: number;
	publishedProducts: number;
	pendingReviewCount: number;
	totalSalesCount: number;
	grossRevenueMinor: number;
}

export interface SellerMonthlySalesPointResponse {
	month: string;
	salesCount: number;
	grossRevenueMinor: number;
}

export interface SellerProductStatsRowResponse {
	productId: number;
	slug: string;
	title: string;
	workflowStatus: string;
	currentPriceMinor: number;
	salesCount: number;
	grossRevenueMinor: number;
	ratingScore: number;
	positiveVotes: number;
	negativeVotes: number;
}

export interface AdminRevisionImageResponse {
	imageId: number;
	originalFilename: string;
	mimeType: string;
	sizeBytes: number;
	sortOrder: number;
	cover: boolean;
	url: string;
}

export interface AdminProductRevisionDetailsResponse {
	revisionId: number;
	revisionNumber: number;
	status: string;
	title: string;
	shortDescription: string;
	priceMinor: number;
	currency: string;
	archiveOriginalFilename: string | null;
	archiveSizeBytes: number | null;
	archiveAttached: boolean;
	archiveUrl: string | null;
	images: AdminRevisionImageResponse[];
	submittedAt: string | null;
	reviewedAt: string | null;
	rejectionReason: string | null;
}

export interface AdminProductDetailsResponse {
	productId: number;
	slug: string;
	state: string;
	sellerPublicName: string;
	currentLiveRevisionId: number | null;
	revisions: AdminProductRevisionDetailsResponse[];
}

export interface AdminProductListItemResponse {
	productId: number;
	slug: string;
	sellerPublicName: string;
	state: string;
	workflowStatus: string;
	title: string;
}

export interface AdminUserListItemResponse {
	userId: number;
	email: string;
	displayName: string;
	enabled: boolean;
	roles: string[];
	seller: boolean;
	availableMinor: number;
	pendingMinor: number;
}

export interface AdminUserDetailsResponse {
	userId: number;
	email: string;
	displayName: string;
	enabled: boolean;
	roles: string[];
	sellerProfile: SellerProfileResponse | null;
	wallet: WalletBalanceResponse;
}

export interface AdminModerationPendingItemResponse {
	revisionId: number;
	productId: number;
	productSlug: string;
	sellerPublicName: string;
	title: string;
	revisionNumber: number;
	submittedAt: string | null;
}

export interface AdminModerationTagResponse {
	id: number;
	name: string;
	slug: string;
}

export interface AdminModerationDetailsResponse {
	revisionId: number;
	productId: number;
	productSlug: string;
	productState: string;
	sellerPublicName: string;
	revisionNumber: number;
	status: string;
	title: string;
	shortDescription: string;
	description: string;
	priceMinor: number;
	currency: string;
	tags: AdminModerationTagResponse[];
	submittedAt: string | null;
	reviewedAt: string | null;
	reviewedByUserId: number | null;
	rejectionReason: string | null;
	archiveAttached: boolean;
	currentLiveRevisionId: number | null;
}

export interface ApiFieldError {
	field: string;
	message: string;
}

export interface ApiErrorResponse {
	timestamp: string;
	status: number;
	error: string;
	message: string;
	path: string;
	fieldErrors: ApiFieldError[];
}

export interface SessionState {
	isAuthenticated: boolean;
	user: AuthUserResponse | null;
}