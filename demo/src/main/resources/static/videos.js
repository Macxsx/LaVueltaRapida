// ===============================
//  F1 OFFICIAL YOUTUBE VIDEOS
// ===============================

// ⚠️ API Key
const API_KEY = "AIzaSyDduusVLhf4BehAD7GnYLLnAeopFPefsdo";

const CHANNEL_ID = "UCB_qr75-ydFVKSF9Dmo6izg"; // Canal Formula 1
const MAX_RESULTS = 3;

async function loadVideos() {
  const videoGrid = document.getElementById("videoGrid");

  if (!videoGrid) return;

  try {
    const url = `https://www.googleapis.com/youtube/v3/search?key=${API_KEY}&channelId=${CHANNEL_ID}&part=snippet&order=date&maxResults=${MAX_RESULTS}`;

    const response = await fetch(url);
    const data = await response.json();

      data.items.slice(0, 8).forEach(item => {
      if (item.id.videoId) {

        const videoCard = document.createElement("div");
        videoCard.classList.add("video-card");

        videoCard.innerHTML = `
          <a href="https://www.youtube.com/watch?v=${item.id.videoId}" target="_blank">
            <div class="video-thumbnail">
              <img src="${item.snippet.thumbnails.high.url}" alt="${item.snippet.title}">
            </div>
            <div class="video-info">
              <div class="video-title">${item.snippet.title}</div>
              <div class="video-date">
                ${new Date(item.snippet.publishedAt).toLocaleDateString()}
              </div>
            </div>
          </a>
        `;

        videoGrid.appendChild(videoCard);
      }
    });

  } catch (error) {
    console.error("Error loading F1 videos:", error);
    videoGrid.innerHTML = "<p>No se pudieron cargar los videos.</p>";
  }
}

// Ejecutar cuando el DOM esté listo
document.addEventListener("DOMContentLoaded", loadVideos);

const buttons = document.querySelectorAll(".ranking-btn");
const tables = document.querySelectorAll(".ranking-table");

buttons.forEach(btn => {
  btn.addEventListener("click", () => {
    buttons.forEach(b => b.classList.remove("active"));
    tables.forEach(t => t.classList.remove("active-table"));

    btn.classList.add("active");
    document.getElementById(btn.dataset.target)
      .classList.add("active-table");
  });
});