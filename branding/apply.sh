#!/usr/bin/env bash
# Aplica la personalizacion "Inteliworks Cloud" sobre un checkout de nextcloud/ios
# (https://github.com/nextcloud/ios) en el tag de UPSTREAM_TAG.
# Uso: branding/apply.sh <dir-upstream>
# SPDX-License-Identifier: GPL-3.0-or-later
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
UP="${1:?uso: apply.sh <dir-upstream>}"
APP_ID="cl.inteliworks.cloud"          # bundle id de la app (extensiones: cl.inteliworks.cloud.<Ext>)
GROUP="group.cl.inteliworks.cloud"     # App Group compartido app <-> extensiones
GROUP_APPS="group.cl.inteliworks.apps" # App Group compartido con futuras apps (Talk iOS)
NAME="Inteliworks Cloud"
PBX="$UP/Nextcloud.xcodeproj/project.pbxproj"
BRAND="$UP/Brand"
die() { echo "ERROR: $*"; exit 1; }
[ -f "$PBX" ] && [ -f "$BRAND/NCBrand.swift" ] || die "no parece un checkout de nextcloud/ios"

# 1) Bundle IDs (app + extensiones) y equipo de desarrollo (lo pone el paso de firma)
perl -pi -e 's/it\.twsweb\.\$\(PRODUCT_NAME:rfc1034identifier\)/'"$APP_ID"'/g;
             s/"?it\.twsweb\.Nextcloud\.([A-Za-z-]+)"?/"'"$APP_ID"'.$1"/g;
             s/DEVELOPMENT_TEAM = NKUJUXUJ3B;/DEVELOPMENT_TEAM = "";/g' "$PBX"
! grep -E 'PRODUCT_BUNDLE_IDENTIFIER = "?it\.twsweb\.Nextcloud[";.]' "$PBX" || die "quedaron bundle ids upstream"
grep -q "PRODUCT_BUNDLE_IDENTIFIER = \"$APP_ID.Share\"" "$PBX" || die "no se cambio el bundle id de Share"

# 2) App Groups y keychain compartido (entitlements + plists + NCBrand)
grep -rlE 'it\.twsweb\.Crypto-Cloud|group\.com\.nextcloud\.apps' "$BRAND" | xargs -r perl -pi -e \
  's/group\.it\.twsweb\.Crypto-Cloud/'"$GROUP"'/g; s/\$\(AppIdentifierPrefix\)it\.twsweb\.Crypto-Cloud/\$(AppIdentifierPrefix)'"$APP_ID"'/g; s/group\.com\.nextcloud\.apps/'"$GROUP_APPS"'/g'
! grep -rqE 'it\.twsweb\.Crypto-Cloud|group\.com\.nextcloud\.apps' "$BRAND" || die "quedaron grupos upstream en Brand/"

# 3) Nombre visible (app y extensiones) y URL name
for p in "$BRAND"/*.plist; do
  perl -0pi -e 's#(<key>CFBundleDisplayName</key>\s*<string>)Nextcloud( Assistant)?(</string>)#$1'"$NAME"'$2$3#' "$p"
done
perl -pi -e 's#<string>it\.twsweb\.Nextcloud</string>#<string>'"$APP_ID"'</string>#' "$BRAND/iOSClient.plist"
grep -q "<string>$NAME</string>" "$BRAND/iOSClient.plist" || die "no se cambio CFBundleDisplayName"

# 4) Opciones de marca (NCBrand.swift)
S="$BRAND/NCBrand.swift"
set_opt() { # set_opt <var> <valor-swift>
  perl -pi -e 's#^(\s*var '"$1"': [A-Za-z]+ = ).*$#${1}'"$2"'#' "$S"
  grep -qF -- "= $2" "$S" || die "no se pudo fijar $1"
}
set_opt brand '"Inteliworks Cloud"'
set_opt brandUserAgent '"InteliworksCloud"'
set_opt textCopyrightNextcloudiOS '"Inteliworks Cloud for iOS %@ (basado en Nextcloud) © 2026"'
set_opt linkLoginHost '"https://inteliworks.cl/cloud/"'
set_opt linkloginPreferredProviders '"https://inteliworks.cl/cloud/"'
set_opt privacy '"https://inteliworks.cl/politica-privacidad.html"'
set_opt sourceCode '"https://github.com/Inteliworks-cl/inteliworks-cloud-ios"'
set_opt appStoreUrl '"https://inteliworks.cl/cloud/"'
set_opt capabilitiesGroup "\"$GROUP\""
set_opt capabilitiesGroupApps "\"$GROUP_APPS\""
set_opt disable_show_more_nextcloud_apps_in_settings 'true'
# Color de marca #1E90D6 (30,144,214)
perl -pi -e 's#(let customer: UIColor = UIColor\()red: [0-9.]+ / 255\.0, green: [0-9.]+ / 255\.0, blue: [0-9.]+ / 255\.0#${1}red: 30.0 / 255.0, green: 144.0 / 255.0, blue: 214.0 / 255.0#' "$S"
grep -q "red: 30.0 / 255.0, green: 144.0 / 255.0, blue: 214.0" "$S" || die "no se cambio el color de marca"

# 5) Iconos y logo
rm -rf "$UP/AppIcon.icon"; cp -r "$HERE/AppIcon.icon" "$UP/AppIcon.icon"
cp "$HERE/Custom.xcassets/AppIcon.appiconset/Icon-App-1024x1024@1x.png" "$BRAND/Custom.xcassets/AppIcon.appiconset/"
rm -f "$BRAND/Custom.xcassets/logo.imageset/"*
cp "$HERE/Custom.xcassets/logo.imageset/"* "$BRAND/Custom.xcassets/logo.imageset/"

echo "Personalizacion iOS aplicada: $APP_ID / $GROUP"
