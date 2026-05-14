document.addEventListener('DOMContentLoaded', () => {
    
    
    const formularioRegistro = document.getElementById('formRegistro');
    
    if (formularioRegistro) {
        formularioRegistro.addEventListener('submit', (e) => {
            e.preventDefault(); 

            const usuario = {
                nombre: document.getElementById('nombre').value,
                email: document.getElementById('email').value,
                telefono: document.getElementById('telefono').value,
                peso: document.getElementById('peso').value,
                altura: document.getElementById('altura').value,
                password: document.getElementById('password').value
            };

            localStorage.setItem('datosUsuario', JSON.stringify(usuario));

            
            simularEnvioEmail(usuario.email);
        });
    }

    const formularioLogin = document.getElementById('formLogin');
    
    if (formularioLogin) {
        formularioLogin.addEventListener('submit', (e) => {
            e.preventDefault();

            const emailEntrada = document.getElementById('loginEmail').value;
            const passEntrada = document.getElementById('loginPassword').value;

        
            const datosGuardados = localStorage.getItem('datosUsuario');

            if (!datosGuardados) {
                alert("Primero debes registrarte.");
                return;
            }

            const usuario = JSON.parse(datosGuardados);

        
            if (emailEntrada === usuario.email && passEntrada === usuario.password) {
                alert("¡Bienvenido/a a Calo-Counter!");
                window.location.href = 'aplicativo.html'; 
            } else {
                alert("Email o contraseña incorrectos.");
            }
        });
    }
});

function simularEnvioEmail(correo) {
    alert("📧 CORREO SIMULADO:\nSe ha enviado un mensaje de confirmación a: " + correo);
    window.location.href = 'login.html';
}

document.addEventListener('DOMContentLoaded', () => {
    const datos = JSON.parse(localStorage.getItem('datosUsuario'));

    if (document.getElementById('verNombre') && datos) {
        document.getElementById('verNombre').textContent = datos.nombre;
        document.getElementById('verEmail').textContent = datos.email;
        document.getElementById('verTel').textContent = datos.telefono;
        document.getElementById('verPeso').textContent = datos.peso;
        document.getElementById('verAltura').textContent = datos.altura;
    }


    if (document.getElementById('resultadoApp') && datos) {
       
        const caloriasBase = parseInt(datos.peso) * 30;
        document.getElementById('resultadoApp').innerHTML = `
            <h3 style="color: #FF5722;">${caloriasBase} kcal</h3>
            <p>Este es tu consumo diario recomendado para mantener tu peso de ${datos.peso} kg.</p>
        `;
    }
});