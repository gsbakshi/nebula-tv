# Security Policy

## Supported versions

| Version | Supported |
|---------|-----------|
| `main` (latest) | ✅ |
| Older commits | ❌ |

Nebula is pre-1.0 and does not yet have versioned releases. Security fixes are applied to `main` only.

## Reporting a vulnerability

**Please do not open a public GitHub issue for security vulnerabilities.**

Report vulnerabilities privately via [GitHub Security Advisories](https://github.com/gsbakshi/nebula-tv/security/advisories/new). This keeps details confidential until a fix is available.

### What to include

- Description of the vulnerability and its potential impact
- Steps to reproduce or a proof-of-concept
- Affected component (e.g. browser panel, app grid, GeckoView integration)
- Any suggested mitigations, if you have them

### What to expect

- **Acknowledgement** within 5 business days
- **Status update** within 14 days (confirmed, in progress, or out of scope)
- Credit in the fix commit and release notes if you'd like it

## Scope

This policy covers the Nebula app source code in this repository. It does not cover:

- **GeckoView / Firefox engine** — report to [Mozilla Security](https://www.mozilla.org/en-US/security/bug-bounty/)
- **Android OS** — report to [Android Security](https://source.android.com/docs/security/overview/updates-resources)
- Third-party dependencies bundled by those projects
