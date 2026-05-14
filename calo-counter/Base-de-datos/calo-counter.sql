CREATE DATABASE calo_counter; 

GO 

  

USE calo_counter; 

GO 

  

CREATE TABLE Usuarios ( 

    id_usuario INT IDENTITY(1,1) PRIMARY KEY, 

    nombre VARCHAR(100) NOT NULL, 

    email VARCHAR(150) NOT NULL UNIQUE, 

    password VARCHAR(255) NOT NULL, 

    peso_actual DECIMAL(5,2) NULL, 

    objetivo_peso DECIMAL(5,2) NULL, 

    rol VARCHAR(20) NOT NULL CHECK (rol IN ('Invitado', 'Usuario', 'Creador', 'Administrador')) 

); 

GO 

  

CREATE TABLE Recetas ( 

    id_receta INT IDENTITY(1,1) PRIMARY KEY, 

    titulo VARCHAR(150) NOT NULL, 

    descripcion VARCHAR(MAX) NULL, 

    instrucciones VARCHAR(MAX) NULL, 

    calorias INT NULL, 

    id_autor INT NOT NULL, 

    CONSTRAINT FK_Recetas_Usuarios FOREIGN KEY (id_autor) 

        REFERENCES Usuarios(id_usuario) 

); 

GO 

  

CREATE TABLE Calendario ( 

    id_entrada INT IDENTITY(1,1) PRIMARY KEY, 

    id_usuario INT NOT NULL, 

    id_receta INT NOT NULL, 

    fecha DATE NOT NULL, 

    momento_dia VARCHAR(20) NOT NULL CHECK (momento_dia IN ('Desayuno', 'Almuerzo', 'Cena', 'Snack')), 

    CONSTRAINT FK_Calendario_Usuarios FOREIGN KEY (id_usuario) 

        REFERENCES Usuarios(id_usuario), 

    CONSTRAINT FK_Calendario_Recetas FOREIGN KEY (id_receta) 

        REFERENCES Recetas(id_receta) 

); 

GO 

  

CREATE TABLE Comentarios ( 

    id_comentario INT IDENTITY(1,1) PRIMARY KEY, 

    id_receta INT NOT NULL, 

    id_usuario INT NOT NULL, 

    contenido_texto VARCHAR(MAX) NOT NULL, 

    fecha_publicacion DATETIME NOT NULL DEFAULT GETDATE(), 

    CONSTRAINT FK_Comentarios_Recetas FOREIGN KEY (id_receta) 

        REFERENCES Recetas(id_receta), 

    CONSTRAINT FK_Comentarios_Usuarios FOREIGN KEY (id_usuario) 

        REFERENCES Usuarios(id_usuario) 

); 

GO 

  

CREATE TABLE Ingredientes ( 

    id_ingrediente INT IDENTITY(1,1) PRIMARY KEY, 

    nombre_alimento VARCHAR(150) NOT NULL UNIQUE, 

    calorias_por_unidad DECIMAL(8,2) NULL 

); 

GO 

  

CREATE TABLE Receta_Ingredientes ( 

    id_receta INT NOT NULL, 

    id_ingrediente INT NOT NULL, 

    cantidad DECIMAL(8,2) NOT NULL, 

    unidad_medida VARCHAR(50) NOT NULL, 

    CONSTRAINT PK_Receta_Ingredientes PRIMARY KEY (id_receta, id_ingrediente), 

    CONSTRAINT FK_RecetaIngredientes_Recetas FOREIGN KEY (id_receta) 

        REFERENCES Recetas(id_receta), 

    CONSTRAINT FK_RecetaIngredientes_Ingredientes FOREIGN KEY (id_ingrediente) 

        REFERENCES Ingredientes(id_ingrediente) 

); 

GO