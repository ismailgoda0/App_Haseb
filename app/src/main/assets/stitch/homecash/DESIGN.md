---
name: دفتر المحافظ — HomeCash
colors:
  surface: '#fbf8ff'
  surface-dim: '#d7d8f0'
  surface-bright: '#fbf8ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f4f2ff'
  surface-container: '#ececff'
  surface-container-high: '#e5e6ff'
  surface-container-highest: '#dfe1f9'
  on-surface: '#181a2c'
  on-surface-variant: '#454654'
  inverse-surface: '#2c2f41'
  inverse-on-surface: '#f0efff'
  outline: '#757686'
  outline-variant: '#c5c5d7'
  surface-tint: '#3a4fd4'
  primary: '#3348ce'
  on-primary: '#ffffff'
  primary-container: '#4f63e8'
  on-primary-container: '#f7f5ff'
  inverse-primary: '#bbc3ff'
  secondary: '#5a5e6a'
  on-secondary: '#ffffff'
  secondary-container: '#dfe2f0'
  on-secondary-container: '#606470'
  tertiary: '#50586e'
  on-tertiary: '#ffffff'
  tertiary-container: '#697087'
  on-tertiary-container: '#f5f5ff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dfe0ff'
  primary-fixed-dim: '#bbc3ff'
  on-primary-fixed: '#000d60'
  on-primary-fixed-variant: '#1b33bc'
  secondary-fixed: '#dfe2f0'
  secondary-fixed-dim: '#c3c6d3'
  on-secondary-fixed: '#171c25'
  on-secondary-fixed-variant: '#424752'
  tertiary-fixed: '#dbe2fd'
  tertiary-fixed-dim: '#bfc6e0'
  on-tertiary-fixed: '#131b2e'
  on-tertiary-fixed-variant: '#3f465c'
  background: '#fbf8ff'
  on-background: '#181a2c'
  surface-variant: '#dfe1f9'
typography:
  display-lg:
    fontFamily: IBM Plex Sans
    fontSize: 34px
    fontWeight: '700'
    lineHeight: 44px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: IBM Plex Sans
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: IBM Plex Sans
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 30px
    letterSpacing: 0em
  headline-sm:
    fontFamily: IBM Plex Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 26px
    letterSpacing: 0em
  body-lg:
    fontFamily: IBM Plex Sans
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 24px
    letterSpacing: 0em
  body-md:
    fontFamily: IBM Plex Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0em
  body-sm:
    fontFamily: IBM Plex Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 18px
    letterSpacing: 0.01em
  label-numeric:
    fontFamily: IBM Plex Sans
    fontSize: 15px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0em
  caption:
    fontFamily: IBM Plex Sans
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.02em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-mobile: 0.75rem
  margin: 1.25rem
  margin-mobile: 1rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style
This design system defines an Arabic-first, privacy-conscious personal finance interface for modern Android devices. The aesthetic bridges the structural clarity of Material You / M3 with the calm, tactile refinement of bespoke private wealth applications.

### Brand Attributes & Tone
- **Atmospheric Trust & Discretion:** Conveys calm financial authority without clinical coldness. Reassuring, private, and precise.
- **RTL-Native Ergonomics:** Thoughtfully architected for right-to-left layout dynamics, ensuring natural optical balance, bidirectional number handling, and thumb-friendly Android interaction zones.
- **Tactile Soft Fintech:** Employs soft lavender-blue tonal planes, generous rounded geometries, and razor-sharp hairline borders over loud gradients or stark drop shadows.

## Colors

The palette relies on high-luminance lavender grounds paired with a saturated ultramarine accent and disciplined semantic signals.

### Core Tonal Scale
- **Canvas Base (`#F8F9FE`):** Cool, high-key canvas providing soft ambient contrast against white surfaces.
- **Surface Elevation 0 (`#FFFFFF`):** High-priority operational cards, active transaction sheets, and modular widgets.
- **Surface Elevation Tint (`#EEF1FF`):** Atmospheric container fill for grouped records, sub-sections, and metadata clusters.
- **Accent Active (`#4F63E8`):** Deep cobalt-violet reserved for active navigation pivots, primary CTAs, and selected states.
- **Accent Subtle (`#DDE4FF`):** Low-contrast focus halos, active pill backgrounds, and badge foundations.

### Typographic Contrast
- **Content Primary (`#171A2B`):** Dense charcoal-navy for critical balances, ledger figures, and primary titles.
- **Content Secondary (`#7883A8`):** Calibrated slate-periwinkle for transaction timestamps, categories, and wallet tags.
- **Hairline Border (`#E5E9F8`):** Structural demarcation with 1px physical width, balancing pure flat layers with soft depth.

### Semantic Transaction Tokens
- **Incoming / Credit (`#25A987`):** Muted emerald for salary deposits, wallet transfers in, and positive cash flow.
- **Outgoing / Debit (`#E36B75`):** Controlled rose-coral for POS card swipes, outgoing transfers, and bill settlements.
- **Pending / Attention (`#E9A23B`):** Mellow amber for unverified SMS patterns, pending reconciliations, and synthetic mock status tags.

## Typography

The typographic hierarchy is unified under `IBM Plex Sans` (Arabic + Latin glyph set) to guarantee optical consistency between Arabic textual scripts and Latin alphanumeric currency markers.

### Typographic Principles
- **Bidirectional Balance:** Arabic text reads naturally right-to-left while currency prefixes, account digits, and tabulations maintain strict tabular spacing (`font-variant-numeric: tabular-nums`).
- **Hierarchy Scaling:** Total balance cards prioritize clean, high-impact numbers with currency units anchored in `label-numeric` size directly adjacent to the amount without disjointed spacing.
- **Line Heights:** Generously budgeted vertical baselines accommodate Arabic diacritics and character ascenders/descenders without clipping container boundaries.

## Layout & Spacing

Layout geometry follows an 8dp baseline grid scaled for portrait mobile experiences (390dp baseline screen width).

### Canvas Shell & Safe Areas
- **Top Inset:** 48dp dedicated system status bar pad protecting ledger balance headers and contextual profile quick-actions.
- **Bottom Navigation Surface:** 76dp container height providing optimal thumb clearance above the native Android system bar.
- **Horizontal Screen Margins:** Fixed 16dp (`margin-mobile`) to maximize transaction display density without card edges feeling strained.
- **Structural Gutters:** 12dp to 16dp spacing between analytical data cards and ledger feeds.

## Elevation & Depth

Visual separation relies primarily on tonal surfaces and structural borders rather than deep, dark drop shadows.

### Elevation Levels
- **Level 0 (Canvas):** Base background tint (`#F8F9FE`). Non-interactive, continuous.
- **Level 1 (Structural Cards):** Crisp `#FFFFFF` surface framed with a 1dp solid border (`#E5E9F8`). Receives an ambient, ultra-subtle downward tint: `0px 4px 20px -2px rgba(79, 99, 232, 0.04)`.
- **Level 2 (Active Sheets / Elevated Modals):** Crisp `#FFFFFF` surface lifted by `0px 12px 32px -4px rgba(23, 26, 43, 0.08)` and bordered by `#E5E9F8`.
- **Recessed Containers:** Internal metrics use `#EEF1FF` without borders or shadows to create a natural indented plane within white cards.

## Shapes

The design system adopts a soft, ergonomic pill-and-squircle aesthetic characteristic of refined Android fintech apps.

### Corner Radii Guidelines
- **Primary Hero Panel (Main Balance Module):** 28dp radius creates an anchor envelope at the top of the viewport.
- **Ledger & Transaction Cards:** 22dp radius maintains modern tactile friendliness while aligning neatly in feeds.
- **Buttons & Input Targets:** 20dp radius ensures tactile press confidence while matching chip contours.
- **Status Tags & Mini Pills:** Fully rounded / pill geometry (`9999px`) for demo badges, transaction categories, and verification indicators.

## Components

### 1. Buttons
- **Primary Action Button:** 52dp height, `#4F63E8` solid fill, `#FFFFFF` text (`body-lg`, 600 weight), 20dp border-radius. Active state transitions to `#3D50D1`.
- **Secondary / Soft Button:** 52dp height, `#EEF1FF` fill, `#4F63E8` text, 20dp border-radius. Zero border.
- **Ghost Action:** 44dp height, transparent fill, `#7883A8` text, hover/pressed state reveals `#EEF1FF`.

### 2. Transaction List Rows
- **Container:** Borderless flat row or boxed card with `#FFFFFF` background, 16dp padding, and a 1dp `#E5E9F8` bottom divider or 18dp rounded enclosure.
- **Visual Icon Anchor:** 44dp circular or squircle container (`#EEF1FF`) containing a 20dp line icon colored by transaction type.
- **Directional Numeric Display:** Right-to-left layout places merchant/SMS source and category on the right, with formatted amounts (`+1,250.00 ج.م` in emerald or `-480.00 ج.م` in coral) flush to the left boundary in tabular numbers.

### 3. Chips & Category Filters
- **Default State:** 36dp height, `#FFFFFF` background, 1dp `#E5E9F8` outline, `#7883A8` text, 18dp corner radius.
- **Selected State:** `#4F63E8` background, `#FFFFFF` text, shadow-free, crisp contrast.

### 4. Demo Mode & Ledger Badges
- **Synthetic Ledger Indicator:** High-visibility pill badge with `#DDE4FF` fill, `#4F63E8` text (`caption` scale, 600 weight), carrying text `وضع تجريبي — بيانات وهمية فقط` to clearly demarcate unlinked/mock local data.
- **SMS Parser Status Pill:** Compact pill badge utilizing `#25A987` tint for confirmed auto-extracted SMS records, and `#E9A23B` tint for pending approval items.

### 5. Input Fields
- **Container Structure:** 56dp height, `#FFFFFF` background, 1dp `#E5E9F8` border, 20dp corner radius, 16dp inline padding.
- **Focus State:** Border changes to 1.5dp `#4F63E8` with a soft 3dp `#DDE4FF` external ring glow.
- **Labels:** Positioned above the field right-aligned (`body-sm`, `#7883A8`).

### 6. Bottom Navigation Bar
- **Bar Frame:** 76dp height, `#FFFFFF` background, fixed bottom attachment, hairline top border (`#E5E9F8`).
- **Tab Elements:** 4 slots (الرئيسية، المعاملات، التقارير، الإعدادات). Active tab utilizes a filled line icon with `#4F63E8` coloring and a pill background indicator in `#EEF1FF`. Inactive tabs render in `#7883A8`.