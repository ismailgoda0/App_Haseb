
### Product truth and privacy

-   HomeCash monitors supported wallet transaction SMS and converts
    recognized messages into local transaction records and reports.
-   Do not imply that HomeCash sends money, performs wallet transfers,
    connects directly to banks, or provides live bank balances unless
    that is explicitly confirmed.
-   Clearly label all sample records and figures as demo data. Totals
    must reconcile with visible sample transactions when presented as
    derived data.
-   Treat SMS and financial records as sensitive. Avoid showing full SMS
    bodies or complete sensitive identifiers on dashboards.
-   Explain why SMS permission is needed in plain language. Do not claim
    encryption, cloud sync, or specific storage guarantees unless
    confirmed.
-   "الدخول كمطوّر" opens a clearly labeled demo mode with fictional data
    only. It must never appear to create real transactions or modify
    real records.
-   If existing authentication is not confirmed, do not invent
    username/password, OTP, password reset, biometric login, account
    registration, or backend authentication. Design a simplified entry
    screen instead.

### Visual direction

Create a calm, trustworthy, premium fintech interface: - White and
near-white background, subtle lavender-blue ambient shapes and
restrained gradients. - One primary accent family: medium blue-violet
for primary buttons, selected navigation, focus and key data. Lighter
tints may be used for surfaces. - Charcoal/navy primary text and muted
blue-gray secondary text. - Muted green for incoming/positive values,
restrained coral for outgoing/negative values, amber for warnings. These
are semantic status colors, not competing brand accents. - Large panels:
24--30 dp radius; standard cards: 18--24 dp; buttons and fields: 18--26
dp. - Subtle borders and low-elevation shadows. Use whitespace and
tinted surfaces before adding shadows. - Consistent outline icon family
and stroke weight. Never use emoji as icons. - Decorative shapes must
remain behind content and never reduce contrast. - Avoid neon glow,
excessive glassmorphism, heavy gradients, generic admin-dashboard
styling, and clutter.

### Arabic typography

-   Primary font: IBM Plex Sans Arabic, Tajawal, or Cairo; choose one
    family and use it consistently. If unavailable, use a close modern
    Arabic sans-serif.
-   Screen title: 24--28 sp, semibold/bold.
-   Section title: 18--22 sp, semibold.
-   Main financial amount: 28--36 sp, bold, tabular numerals if
    available.
-   Body: 14--16 sp; supporting labels: 12--14 sp. Do not make important
    content tiny.
-   Use natural concise Arabic. Correct RTL alignment, punctuation, and
    amount/currency order.
-   Currency examples should be consistent, e.g. `1,250.00 ج.م`. Do not
    mix formats.

### Layout and interaction

-   Mobile-first Android, designed for 360--430 dp wide screens and
    taller/narrower devices.
-   Respect status/navigation bars and safe areas. No important controls
    beneath system bars.
-   Use 20--24 dp horizontal page gutters and an 8 dp spacing rhythm.
-   Minimum touch target: 48 dp.
-   Use consistent bottom navigation with four destinations: **الرئيسية،
    المعاملات، التقارير، الإعدادات**. A plus action is allowed only if
    its function is clear and does not falsely imply sending money.
-   Use a clear app bar with only relevant actions. No overlapping
    elements, clipped Arabic, horizontal overflow, or illegible chart
    labels.
-   Design populated, loading, empty, permission-denied,
    paused-monitoring, parsing-warning and error states.

## 2. Screens to generate

Create these screens using the same design system and connected
navigation.

### Onboarding and entry

1.  **Splash:** wallet mark, "دفتر المحافظ", tagline "تابع معاملات
    محافظك بسهولة", white/lavender background.
2.  **Onboarding --- التتبع:** explain supported wallet transaction SMS
    can be organized into a ledger.
3.  **Onboarding --- التقارير:** show incoming/outgoing summaries and
    date filters.
4.  **Onboarding --- الخصوصية:** explain SMS permission and user control
    without unsupported security claims.
5.  **Login/entry:** match the references: logo, title, short subtitle,
    primary blue-violet "تسجيل الدخول" only if real authentication
    exists, divider "أو استكشف التطبيق", secondary "الدخول كمطوّر", and
    privacy reassurance. If authentication is not confirmed, do not add
    fake credential fields.
6.  **Demo notice:** prominent "وضع تجريبي --- بيانات وهمية فقط",
    fictional sample records, exit/reset action.
7.  **SMS permission education:** explain why access is needed, buttons
    "متابعة" and "ليس الآن". This is an in-app explanation, not a fake
    Android system dialog.

### Dashboard

8.  **Home --- populated:** greeting/date range; prominent "إجمالي
    الوارد" and "إجمالي الصادر"; net movement only if valid and clearly
    defined; one weekly/monthly trend chart; recent transactions;
    wallet/provider marker; date/time; amount and direction; actual
    monitoring/last-sync status only when available.
9.  **Home --- empty:** explain how to enable SMS permission or import
    supported data if that feature exists. Provide a clear next step.
10. **Permission denied/monitoring paused:** calm explanation and
    retry/open-settings action; never imply monitoring is active when it
    is not.

### Transactions

11. **Transaction list:** search, date range, provider filter, direction
    filter (وارد/صادر), amount, source label, tidy separators.
12. **Transaction detail:** amount, direction, provider, date/time,
    recognized fields, and "مصدر البيانات: رسالة SMS". Mask sensitive
    identifiers by default.
13. **Search/filter bottom sheet:** date range, provider, direction,
    apply and reset.
14. **Uncertain/duplicate message review:** indicate uncertain parsing
    or possible duplicate; never fabricate missing values.

### Reports

15. **Reports overview:** week/month/year filters, incoming/outgoing
    totals, valid net movement, clear bar/line chart.
16. **Provider breakdown:** comparison by detected provider, compact
    legend and accessible semantic colors.
17. **Timeline:** transactions grouped by day with daily totals.
18. **Export/share preview:** show only formats actually supported;
    unavailable actions must be labeled honestly.

### Settings and privacy

19. **Settings:** monitoring status, SMS permissions, language,
    appearance, privacy, demo mode, app info.
20. **Privacy/data controls:** explain what is read and stored; show
    delete/manage actions only if supported.
21. **Provider recognition:** recognized provider names and status,
    without implying integrations that do not exist.
22. **Developer/demo controls:** seed/reset demo data, exit demo mode,
    persistent "بيانات تجريبية فقط" label. Never let a demo reset
    accidentally delete real records.

### Reusable states

23. Dashboard and transaction loading skeletons.
24. Empty search results.
25. Inline error and parsing warning.
26. Confirmation dialog for destructive actions.
27. Success/failure snackbar.
28. Transaction actions and filter bottom sheets.

## 3. Sample data and copy

Use coherent fictional Arabic sample data and one currency style: -
وارد: `+1,250.00 ج.م` - صادر: `−320.00 ج.م` - Descriptions: "استلام
تحويل"، "شراء"، "إيداع محفظة" - Source: "رسالة SMS" - Statuses: "تم
التعرّف عليها"، "تحتاج مراجعة"، "المراقبة متوقفة"

Make displayed totals reconcile with visible records when derived. If
illustrative, label the screen "بيانات تجريبية". Never show fake
transaction-success confirmations that imply money moved.

## 4. DESIGN.md --- single source of truth

Create a `DESIGN.md` that defines the atmosphere, tokens, typography,
component variants, spacing, motion, accessibility, and explicit
anti-patterns. Use these initial tokens consistently:

### Color tokens

-   Canvas / background: `#F8F9FE`
-   Primary surface: `#FFFFFF`
-   Soft lavender-blue surface: `#EEF1FF`
-   Primary blue-violet accent: `#4F63E8`
-   Accent tint: `#DDE4FF`
-   Primary text: `#171A2B`
-   Secondary text: `#7883A8`
-   Hairline border: `#E5E9F8`
-   Incoming/positive: `#25A987`
-   Outgoing/negative: `#E36B75`
-   Warning: `#E9A23B`

Use one brand accent consistently. Keep semantic colors muted. Maintain
strong readable contrast; never place pale text on white.

### Component system

Specify default, pressed, selected, disabled, loading and error states
where relevant for: - Primary/secondary/text/destructive buttons -
Labeled inputs, focus/error/disabled/password visibility states only if
authentication exists - Metric cards, transaction rows and provider
badges - Bottom navigation, tabs and segmented controls - Charts,
legends and date filters - Monitoring/permission/parsing status badges -
Dialogs, bottom sheets, snackbars - Loading, empty, permission-denied
and error states

### Shape and spacing

-   8 dp spacing rhythm
-   Page gutters 20--24 dp
-   Major panels 24--30 dp radius
-   Standard cards 18--24 dp radius
-   Inputs/buttons 18--26 dp radius
-   Subtle 1 dp borders; low-elevation shadows
-   Minimum 48 dp touch target

### Motion

Use restrained transitions around 180--280 ms and gentle spring-like
selection/press feedback. Prefer opacity and transforms. Respect
reduced-motion/accessibility preferences. Do not animate financial data
continuously or make status indicators pulse without meaning.

### Accessibility

High-contrast text; at least 48 dp touch targets; direction and amount
must not be communicated by color alone; support long Arabic
wallet/provider names and system text scaling; provide meaningful
descriptions for icons and chart summaries; avoid clipped amounts.

## 5. Anti-patterns --- never generate

-   Generic admin dashboard or tiny dense tables.
-   Multiple competing saturated accent colors, neon glows or heavy
    gradients.
-   Emoji icons, generic Latin placeholder names, or awkward translated
    Arabic.
-   Three equal dashboard cards squeezed across a phone screen.
-   Long paragraphs inside cards or excessive card nesting.
-   Fake money transfers, fake successful transactions, unsupported bank
    integrations or invented live balances.
-   A plus button that implies "send money" when the app only monitors
    SMS.
-   Unsupported sign-up, OTP, password reset, biometric, cloud-sync or
    bank-linking flows.
-   Hidden SMS permission status or full sensitive SMS bodies on the
    dashboard.
-   Inconsistent sample totals, tiny chart labels, low-contrast text,
    incorrect RTL, clipped content or horizontal overflow.
-   UI work that changes business logic or invents APIs during this
    design-only phase.

## 6. Stitch workflow

1.  Attach all three supplied reference images to the Stitch project.
2.  Paste the Master Prompt in section 1.
3.  Ask Stitch to create `DESIGN.md` first and treat it as the single
    source of truth.
4.  Generate screens in batches: entry/onboarding; dashboard;
    transactions/details; reports; settings/privacy; then
    empty/loading/error states.
5.  Reuse identical tokens, typography, radii, navigation and component
    behavior across every batch.
6.  Create a clickable prototype with realistic navigation. Demo mode
    must remain clearly separate from real records.
7.  Export all screens, reusable components and assets where supported,
    plus `DESIGN.md` and a screen inventory.
8.  Do not merge the design into the Android codebase yet. The UI will
    be integrated later after review.

## 7. First instruction to Stitch

Start by generating only these four high-fidelity screens: **(1)
populated Home dashboard, (2) transaction list with filters, (3)
transaction details, (4) login/entry screen with "الدخول كمطوّر" clearly
labeled as demo**. Establish the shared design system and bottom
navigation on these screens before generating the rest. Prioritize
reference fidelity, Arabic RTL, consistent spacing, believable finance
data, and a premium fintech aesthetic. Do not generate code or change
application logic yet.

## Acceptance checklist

-   [ ] Arabic RTL is correct on all Arabic screens.
-   [ ] Login/entry screen follows the supplied white/lavender/blue
    style.
-   [ ] Dashboard, transactions, details, reports and settings feel like
    one product.
-   [ ] Demo data is visibly labeled and separate from real data.
-   [ ] No unsupported money-transfer or bank-integration claims.
-   [ ] Empty, loading, error, permission-denied and populated states
    are included.
-   [ ] Layout remains readable at 360 dp width.
-   [ ] No clipping, overlap, or horizontal overflow.
-   [ ] Clickable prototype and consistent bottom navigation are
    provided.
-   [ ] `DESIGN.md` and complete screen inventory are delivered.
-   [ ] Design remains separate from business logic and ready for later
    Android integration.
