## Dita 2 — Arkitektura CardLayout & Menu Kryesore

**Objektivi:** Kalimi nga një ekran i vetëm në një arkitekturë me shumë ekrane.

- **MainFrame (Controller):** `CardLayout` menaxhon kalimin mes `MenuPanel` dhe `GamePanel` me çelësa string (`"MENU"`, `"GAME"`)
- **MenuPanel:** Panel i dedikuar për menunë me background imazh dhe 3 butona (Play, Settings, Exit)
- **GridBagLayout:** Pozicionimi i butonave në kolonë të centruar mbi imazhin e background-it
- **Butonat Custom:** `paintComponent` i override-uar — sfond i errët gjysmë-transparent + border cyan neon + efekt hover
- **ComponentListener:** `GamePanel` starton/ndalon loop-in automatikisht kur `CardLayout` e shfaq/fsheh
- **FlatDarkLaf:** Tema e errët e aplikuar globalisht para krijimit të çdo komponenti Swing
- **Settings Dialog:** `JDialog` modal si placeholder për opsionet e ardhshme
