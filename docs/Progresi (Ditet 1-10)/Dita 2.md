 Dita 2 — Arkitektura CardLayout & Menu Kryesore

Objektivi: Kalimi nga një ekran i vetëm në një arkitekturë me shumë ekrane.

-1.MainFrame (Controller): `CardLayout` menaxhon kalimin mes `MenuPanel` dhe `GamePanel` me çelësa string (`"MENU"`, `"GAME"`)

-2.MenuPanel:	 Panel i dedikuar për menunë me background imazh dhe 3 butona (Play, Settings, Exit)

-3.GridBagLayout: Pozicionimi i butonave në kolonë të centruar mbi imazhin e background-it

-4.Butonat Custom: `paintComponent` i override-uar — sfond i errët gjysmë-transparent + border cyan neon + efekt hover

-5.ComponentListener: `GamePanel` starton/ndalon loop-in automatikisht kur `CardLayout` e shfaq/fsheh

-6.FlatDarkLaf:		 Tema e errët e aplikuar globalisht para krijimit të çdo komponenti Swing

-7.Settings Dialog: `JDialog` modal si placeholder për opsionet e ardhshme
