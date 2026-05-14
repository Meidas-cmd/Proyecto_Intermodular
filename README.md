#  Calo-Counter

Aplicación web para controlar calorías diarias y gestionar recetas saludables. Proyecto intermodular desarrollado en 1º DAM.

---

##  ¿Qué hace este proyecto?

Calo-Counter es una aplicación web que permite a los usuarios:

- **Registrarse e iniciar sesión** con sus datos personales (nombre, email, peso, altura)
- **Calcular las calorías diarias recomendadas** según su peso
- **Ver su perfil** con los datos introducidos al registrarse
- **Gestionar recetas** con información nutricional (en base de datos)

---

##  Estructura del proyecto

```
calo-counter/
├── landing/
│   ├── index.html         → Página de bienvenida
│   ├── login.html         → Formulario de inicio de sesión
│   ├── registrarse.html   → Formulario de registro
│   ├── aplicativo.html    → App principal con cálculo de calorías
│   ├── perfil.html        → Perfil del usuario
│   ├── script.js          → Lógica del frontend (JS)
│   ├── style.css          → Estilos de la aplicación
│   └── logo.png           → Logo del proyecto
├── Base-de-datos/
│   └── calo-counter.sql   → Script SQL para crear la base de datos
└── pdfs/
    └── CALO-COUNTER.pdf   → Documentación del proyecto
```

---

##  Tecnologías utilizadas

| Tecnología | Para qué se usa |
|---|---|
| HTML5 | Estructura de las páginas |
| CSS3 | Diseño y estilos |
| JavaScript | Lógica del frontend y localStorage |
| SQL Server | Base de datos (tablas de usuarios, recetas, calendario) |

---

##  Cómo ejecutar el proyecto

1. **Clona el repositorio:**
   ```bash
   git clone https://github.com/Meidas-cmd/Proyecto_Intermodular.git
   ```

2. **Abre el proyecto** en Visual Studio Code (o tu editor favorito)

3. **Lanza la página principal** abriendo `landing/index.html` en el navegador  
   *(Se recomienda usar la extensión Live Server de VS Code)*

4. **Base de datos** (opcional): ejecuta el script `Base-de-datos/calo-counter.sql` en SQL Server si quieres montar la parte de base de datos

---

##  Autores

Proyecto desarrollado por alumnos Christian Madueño, Gabriel Tortosa, Manuel Pastor, Kevin Muñoz y Jorge Cuartero de **1º DAM** como proyecto intermodular.

---

##  Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo [LICENSE](LICENSE.md) para más información.
