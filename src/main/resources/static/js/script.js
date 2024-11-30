$(function(){

// User Register validation

	var $userRegister=$("#userRegister");

	$userRegister.validate({

		rules:{
			name:{
				required:true,
				lettersonly:true
			}
			,
			email: {
				required: true,
				space: true,
				email: true
			},
			phoneNumber: {
				required: true,
				space: true,
				numericOnly: true,
				minlength: 10,
				maxlength: 12

			},
			password: {
				required: true,
				space: true

			},
			confirmPassword: {
				required: true,
				space: true,
				equalTo: '#password'

			},
			address: {
				required: true,
				all: true

			},

			city: {
				required: true,
				space: true

			},
			state: {
				required: true,


			},
			pincode: {
				required: true,
				space: true,
				numericOnly: true

			}, file: {
				required: true,
			}

		},
		messages:{
			name:{
				required:'Please enter your name',
				lettersonly:'Please enter a valid name'
			},
			email: {
				required: 'Please enter your email address',
				space: 'Spaces are not allowed',
				email: 'Please enter a valid email'
			},
			phoneNumber: {
				required: 'Please enter your Mobile Number',
				space: 'Spaces are not allowed',
				numericOnly: 'Please enter a valid mobile number',
				minlength: 'Min 10 digit is required',
				maxlength: 'Max 12 digit is required'
			},

			password: {
				required: 'Please enter a password',
				space: 'Spaces are not allowed'

			},
			confirmPassword: {
				required: 'Please re-enter the password',
				space: 'Spaces are not allowed',
				equalTo: 'Please re-enter the same password'

			},
			address: {
				required: 'Please Enter an address',
				all: 'Invalid'

			},
			file: {
				required: 'Please select your Profile Picture',
			}
		}
	}),

	// Reset Password Validation

    var $resetPassword=$("#resetPassword");

    $resetPassword.validate({

    		rules:{
    			password: {
    				required: true,
    				space: true

    			},
    			confirmPassword: {
    				required: true,
    				space: true,
    				equalTo: '#newPassword'

    			}
    		},
    		messages:{
    		   password: {
    				required: 'password must be required',
    				space: 'space not allowed'

    			},
    			confirmPassword: {
    				required: 'confirm password must be required',
    				space: 'space not allowed',
    				equalTo: 'password mismatch'

    			}
    		}
    })
})

jQuery.validator.addMethod('lettersonly', function(value, element) {
		return /^[^-\s][a-zA-Z_\s-]+$/.test(value);
	});

	jQuery.validator.addMethod('space', function(value, element) {
    		return /^[^-\s]+$/.test(value);
    	});

    	jQuery.validator.addMethod('all', function(value, element) {
    		return /^[^-\s][a-zA-Z0-9_,.\s-]+$/.test(value);
    	});


    	jQuery.validator.addMethod('numericOnly', function(value, element) {
    		return /^[0-9]+$/.test(value);
    	});