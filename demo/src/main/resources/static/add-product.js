  const imageInput = document.getElementById('image');
  const previewImg = document.getElementById('previewImg');
  const placeholder = document.querySelector('.preview-placeholder');

  function loadPreview(url) {
    if (url) {
      previewImg.src = url;
      previewImg.style.display = 'block';
      placeholder.style.display = 'none';
      previewImg.onerror = function () {
        previewImg.style.display = 'none';
        placeholder.style.display = 'flex';
      };
    } else {
      previewImg.style.display = 'none';
      placeholder.style.display = 'flex';
    }
  }

  imageInput.addEventListener('input', function () {
    loadPreview(this.value.trim());
  });

  if (imageInput.value.trim()) {
    loadPreview(imageInput.value.trim());
  }

  const toggle = document.getElementById('availableToggle');
  const toggleText = document.getElementById('toggleText');

  function updateToggleState() {
    if (toggle.checked) {
      toggleText.textContent = 'Disponible en el menú';
    } else {
      toggleText.textContent = 'No disponible';
    }
  }

  toggle.addEventListener('change', updateToggleState);
  updateToggleState();