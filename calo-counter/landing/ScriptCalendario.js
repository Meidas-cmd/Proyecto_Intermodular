
const CLIENT_ID = "116544285744-5t4hsbcaho0hb30g2u3848i3g5ch52mh.apps.googleusercontent.com";
const API_KEY = "AIzaSyBKF6JikRacCoou0PHQkYVBpnvCXUymIhw";
const DISCOVERY_DOC = "https://www.googleapis.com/discovery/v1/apis/calendar/v3/rest";
const SCOPES = "https://www.googleapis.com/auth/calendar.events";

let tokenClient;
let gapiInited = false;
let gisInited = false;


window.onload = () => {
    gapi.load("client", async () => {
        await gapi.client.init({
            apiKey: API_KEY,
            discoveryDocs: [DISCOVERY_DOC],
        });
        gapiInited = true;
        console.log("GAPI listo");

       
        tokenClient = google.accounts.oauth2.initTokenClient({
            client_id: CLIENT_ID,
            scope: SCOPES,
            callback: async (tokenResponse) => {
                if (tokenResponse.error) {
                    console.error("Error auth:", tokenResponse);
                    return;
                }
                document.getElementById("btn-login").style.display = "none";
                document.getElementById("btn-logout").style.display = "inline-block";
                document.getElementById("calendar-content").style.display = "block";

                
                await new Promise(r => setTimeout(r, 300));
                obtenerEventos();
            },
        });
        gisInited = true;
        console.log("GIS listo");
    });
};

document.getElementById("btn-login").onclick = () => {
    if (!tokenClient) {
        alert("Google aún se está cargando, espera un momento.");
        return;
    }
    tokenClient.requestAccessToken({ prompt: "consent" });
};

document.getElementById("btn-logout").onclick = () => {
    const token = gapi.client.getToken();
    if (token) {
        google.accounts.oauth2.revoke(token.access_token, () => {
            console.log("Token revocado");
        });
        gapi.client.setToken(null);
    }
    document.getElementById("btn-login").style.display = "inline-block";
    document.getElementById("btn-logout").style.display = "none";
    document.getElementById("calendar-content").style.display = "none";
    document.getElementById("lista-eventos").innerHTML = "";
};


async function obtenerEventos() {
    try {
        const response = await gapi.client.calendar.events.list({
            calendarId: "primary",
            timeMin: new Date().toISOString(),
            showDeleted: false,
            singleEvents: true,
            maxResults: 10,
            orderBy: "startTime",
        });

        const eventos = response.result.items;
        const lista = document.getElementById("lista-eventos");
        lista.innerHTML = "";

        if (!eventos || eventos.length === 0) {
            lista.innerHTML = "<p>No tienes comidas guardadas.</p>";
            return;
        }

        eventos.forEach((evento) => {
            const inicio = evento.start.dateTime || evento.start.date;
            const fecha = new Date(inicio).toLocaleString("es-ES", {
                weekday: "long",
                year: "numeric",
                month: "long",
                day: "numeric",
                hour: "2-digit",
                minute: "2-digit",
            });

            const div = document.createElement("div");
            div.className = "evento-item";
            div.innerHTML = `
                <strong>${evento.summary}</strong>
                <span>${fecha}</span>
                <button onclick="eliminarEvento('${evento.id}')" class="btn-borrar">🗑 Cancelar</button>
            `;
            lista.appendChild(div);
        });

    } catch (err) {
        console.error("Error al obtener eventos:", err);
        alert("Error al cargar tus comidas. Intenta de nuevo.");
    }
}

const TURNOS = {
    desayuno: { inicio: "08:00", fin: "10:00", emoji: "☕" },
    almuerzo: { inicio: "10:30", fin: "12:00", emoji: "🥐" },
    comida:   { inicio: "14:00", fin: "16:00", emoji: "🍽️" },
    merienda: { inicio: "17:00", fin: "19:00", emoji: "🍰" },
    cena:     { inicio: "21:00", fin: "23:00", emoji: "🌙" },
};

const DIAS_ES = {
    MO: "lunes", TU: "martes", WE: "miércoles",
    TH: "jueves", FR: "viernes", SA: "sábado", SU: "domingo"
};

const DIA_JS = { MO:1, TU:2, WE:3, TH:4, FR:5, SA:6, SU:0 };

function proximaFecha(diaCode) {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    const objetivo = DIA_JS[diaCode];
    const diff = (objetivo - hoy.getDay() + 7) % 7;
    const fecha = new Date(hoy);
    fecha.setDate(hoy.getDate() + (diff === 0 ? 7 : diff));
    return fecha;
}


function formatUntil(date) {
    return date.toISOString().replace(/[-:]/g, "").split(".")[0] + "Z";
}

document.getElementById("btn-reservar").onclick = async () => {
    const plato   = document.getElementById("reserva-plato").value;
    const diaCode = document.getElementById("reserva-dia").value;
    const turno   = document.getElementById("reserva-turno").value;

    if (!plato || !diaCode || !turno) {
        alert("Por favor, rellena todos los campos.");
        return;
    }

    const { inicio, fin, emoji } = TURNOS[turno];
    const fechaInicio = proximaFecha(diaCode);
    const fechaFin = new Date(fechaInicio);
    fechaFin.setDate(fechaFin.getDate() + 21); 

    const fechaStr = fechaInicio.toISOString().split("T")[0];

    const evento = {
        summary: `${emoji} ${plato}`,
        description: `Guardado de: ${plato}\nTurno: ${turno} (${inicio} - ${fin})\nDía: ${DIAS_ES[diaCode]}\nCreada desde calo-counter`,
        start: {
            dateTime: `${fechaStr}T${inicio}:00`,
            timeZone: "Europe/Madrid",
        },
        end: {
            dateTime: `${fechaStr}T${fin}:00`,
            timeZone: "Europe/Madrid",
        },
        recurrence: [
            `RRULE:FREQ=WEEKLY;COUNT=4;BYDAY=${diaCode}`
        ],
    };

    try {
        await gapi.client.calendar.events.insert({
            calendarId: "primary",
            resource: evento,
        });

        alert(`✅ Comida guardada: "${plato}" los ${DIAS_ES[diaCode]} durante las próximas 4 semanas.`);

        document.getElementById("reserva-plato").value = "";
        document.getElementById("reserva-dia").value = "";
        document.getElementById("reserva-turno").value = "";

        obtenerEventos();

    } catch (err) {
        console.error("Error al guardar la comida:", err);
        alert("Error al guardar la comida. Comprueba que has iniciado sesión.");
    }
};


document.getElementById("btn-cancelar-todas").onclick = async () => {
    if (!confirm("¿Seguro que quieres borrar TODAS tus comidas de las próximas 4 semanas?")) return;

    try {
        const response = await gapi.client.calendar.events.list({
            calendarId: "primary",
            timeMin: new Date().toISOString(),
            timeMax: new Date(Date.now() + 28 * 24 * 60 * 60 * 1000).toISOString(),
            showDeleted: false,
            singleEvents: true,
            maxResults: 100,
            orderBy: "startTime",
        });

        const eventos = response.result.items;

        if (!eventos || eventos.length === 0) {
            alert("No tienes comidas para borrar.");
            return;
        }

   
        const promesas = eventos.map(ev =>
            gapi.client.calendar.events.delete({
                calendarId: "primary",
                eventId: ev.id,
            })
        );

        await Promise.all(promesas);

        alert(`✅ ${eventos.length} comida(s) borradas correctamente.`);
        obtenerEventos();

    } catch (err) {
        console.error("Error al cancelar:", err);
        alert("Error al borrar las comidas. Inténtalo de nuevo.");
    }
};
