# Supported Languages in the Application

This document outlines the supported languages for the application and how they are configured.

## Supported Languages
The application supports the following languages:

- **English (en)**
- **French (fr)**
- **Spanish (es)**
- **German (de)**
- **Chinese (zh)**

## Complete List of Supported Languages

The application supports the following languages:

- Albanian (al)
- Arabic (ar-ly)
- Azerbaijani (az-Latn-az)
- Bulgarian (bg)
- Bengali (bn)
- Belarusian (by)
- Catalan (ca)
- Czech (cs)
- Danish (da)
- German (de)
- Greek (el)
- English (en)
- Spanish (es)
- Estonian (et)
- Persian (fa)
- Finnish (fi)
- French (fr)
- Galician (gl)
- Hebrew (he)
- Hindi (hi)
- Croatian (hr)
- Hungarian (hu)
- Armenian (hy)
- Indonesian (id)
- Italian (it)
- Japanese (ja)
- Korean (ko)
- Kurdish (kr-Latn-kr)
- Marathi (mr)
- Burmese (my)
- Dutch (nl)
- Punjabi (pa)
- Polish (pl)
- Portuguese (Brazil) (pt-br)
- Portuguese (Portugal) (pt-pt)
- Romanian (ro)
- Russian (ru)
- Sinhala (si)
- Slovak (sk)
- Serbian (sr)
- Swedish (sv)
- Tamil (ta)
- Telugu (te)
- Thai (th)
- Turkish (tr)
- Ukrainian (ua)
- Uzbek (Cyrillic) (uz-Cyrl-uz)
- Uzbek (Latin) (uz-Latn-uz)
- Vietnamese (vi)
- Chinese (Simplified) (zh-cn)
- Chinese (Traditional) (zh-tw)

## Configuration

### 1. Environment Variable
The default language for the application can be set using the `APP_LANGUAGE` environment variable. This variable should be set in the container or `.env` file during deployment.

Example:
```env
APP_LANGUAGE=en
```

### 2. Frontend Configuration
The frontend application reads the `APP_LANGUAGE` environment variable to set the default language. The language dropdown in the UI will reflect the current language and allow users to switch between supported languages.

### 3. Backend Configuration
If the backend needs to handle language-specific data (e.g., localized error messages or API responses), it should also read the `APP_LANGUAGE` environment variable. Ensure that the backend and frontend are synchronized in terms of the default language.

## Deployment
When deploying the application in a containerized environment, ensure the `APP_LANGUAGE` variable is set in the container's environment.

Example Docker Compose configuration:
```yaml
services:
  app:
    image: my-app:latest
    environment:
      - APP_LANGUAGE=en
```

## Removing the Language Dropdown
If the language dropdown is removed from the UI, the application will rely solely on the `APP_LANGUAGE` environment variable to determine the language. Ensure this variable is correctly set during deployment.

## Notes
- If `APP_LANGUAGE` is not set, the application will default to English (`en`).
- Update this document whenever new languages are added or removed from the application.