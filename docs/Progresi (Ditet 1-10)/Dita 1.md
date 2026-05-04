Objektivi: Krijimi i bazës së lojës (game engine) dhe mekanikave të lëvizjes.
Çfarë është realizuar sot:

    Ndërtimi i dritares (GUI): Përdorimi i JFrame dhe JPanel si template për lojën.

    Sistemi i lëvizjes: Implementimi i KeyListener që lejon kontrollin e makinës majtas dhe djathtas me shigjetat e tastierës.

    Vizatimi Grafik (Graphics2D): Makina dhe rruga nuk janë foto, por janë vizatuar me kod duke përdorur forma gjeometrike (fillRect, fillRoundRect). Kjo e bën lojën më të shpejtë.

    Game Loop: Përdorimi i një javax.swing.Timer që rifreskon dritaren çdo 16ms (rreth 60 FPS) për një eksperiencë të rrjedhshme.

Logjika kryesore :

    Scrolling Road: Kam përdorur variablin laneMarkerOffset me operatorin % (modulo) për të krijuar iluzionin sikur rruga po lëviz, ndërkohë që janë vetëm vijat që rinisin pozicionin e tyre.

    Boundary Clamping: Kam vendosur kufij (boundaries) që makina të mos dalë jashtë rrugës (nga koordinata 60 deri në 340).