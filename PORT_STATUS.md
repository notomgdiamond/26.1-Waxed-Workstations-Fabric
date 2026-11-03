# 26.1 Fabric port — first compilation candidate

This project reconstructs the two-class NeoForge mod behavior using Fabric UseBlockCallback. It is NOT compiled or verified. Build via GitHub Actions.

Behavior: sneak-right-click honeycomb on a registered POI to remove POI; sneak-right-click axe to restore. Excludes beds, portals, lightning rods, lodestones. Uses no persistent wax tracking, matching the NeoForge code.

Known caveat: POIs may be regenerated when chunks/blocks update; no persistence layer is implemented. This source may require adjustments to Fabric 26.1 method signatures. Upload build log for troubleshooting.
