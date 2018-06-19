import $ from 'jquery';
import * as M from "../../../vendor/materialize";

export class GreetingComponent {
    constructor() {

    }

    static handleImageChosen(event) {
        const $modal = $(event.currentTarget).parents('.modal');
        $modal.addClass('chosen');
        const img = $modal.find('img').clone();

        const $imgContainer = $('.chosen-img-wrapper');
        $imgContainer.find('img').remove();
        $imgContainer.removeClass('hide').show().prepend(img);
        $('.images-container').hide();
    }

    static handleChangeChoice(event) {
        const $modal = $('.modal.chosen');
        M.Modal.getInstance($modal).close();
        $modal.removeClass('chosen');
        $('.chosen-img-wrapper').hide();
        $('.images-container').show();
    }

    openGalleryPopup(event) {
        // index of image to be opened
        const imgIndex = $(event.currentTarget).attr('href').slice(5);
        console.log(imgIndex);
    }
}