# Inteliworks Cloud — iOS

App iOS **Inteliworks Cloud**: el cliente oficial de [Nextcloud para iOS](https://github.com/nextcloud/ios)
con la marca de Inteliworks. Cada empresa cliente se conecta a **su propio servidor**
(`https://cloud.<empresa>.cl`).

- Bundle ID: `cl.inteliworks.cloud` (extensiones `cl.inteliworks.cloud.<Extension>`), App Group `group.cl.inteliworks.cloud`.
- Soporte: soporte@inteliworks.cl · Privacidad: https://inteliworks.cl/politica-privacidad.html

## Cómo funciona (overlay, igual que Android/desktop)

Este repo **no copia** el código de Nextcloud. `.github/workflows/build.yml`:

1. descarga `nextcloud/ios` en el tag de `UPSTREAM_TAG`,
2. ejecuta `branding/apply.sh` (bundle IDs, App Groups, nombre, colores, URLs, íconos, logo),
3. compila **sin firma** en macOS (valida que el rebrand compila),
4. **solo** si se lanza a mano con `subir=true` y existen los secrets, archiva, firma
   (firma automática de Xcode con la API key de App Store Connect) y sube a App Store Connect/TestFlight.

| Archivo | Qué es |
|---|---|
| `UPSTREAM_TAG` | tag estable de Nextcloud iOS (ej. `35.0.0`; >= 35.1 requiere Xcode 27 en el runner) |
| `branding/apply.sh` | aplica la marca sobre el checkout upstream |
| `branding/AppIcon.icon`, `branding/Custom.xcassets` | ícono (Icon Composer + 1024 px) y logo |
| `tools/GenerarAssetsTiendas.java` | regenera íconos/capturas de tienda desde el logo fuente |

## Secrets (Settings → Secrets and variables → Actions)

`ASC_KEY_ID`, `ASC_ISSUER_ID`, `ASC_KEY_P8_BASE64` (`base64 -w0 AuthKey_XXXX.p8`), `APPLE_TEAM_ID`.
Sin ellos el workflow solo compila sin firma. **Nunca** subir la llave `.p8` al repo.

## Actualizar a una nueva versión de Nextcloud iOS

Editar `UPSTREAM_TAG` (ver https://github.com/nextcloud/ios/releases) → push → si `apply.sh` falla,
upstream cambió `Brand/NCBrand.swift`, los plists o el `project.pbxproj`: ajustar.
El número de build (`CFBundleVersion`) es el número de corrida de Actions (siempre crece).

## Notificaciones push

Upstream solo activa el proxy de push de Nextcloud cuando la marca es "Nextcloud". Con marca propia las
notificaciones push quedan **desactivadas** hasta tener un proxy de push propio con certificado APNs
de Inteliworks (fase 2).

## Licencia

Basado en Nextcloud iOS © Nextcloud GmbH y colaboradores, **GPL-3.0-or-later** (ver `LICENSE.txt`).
